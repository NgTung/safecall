package org.safecall.circuitbreaker

import java.util.concurrent.atomic.{AtomicInteger, AtomicLong, AtomicReference}
import java.util.concurrent.{Callable, Executors, TimeUnit}

import scala.util.{Failure, Success, Try}

class CircuitBreaker(failureThreshold: Int, timeout: Long, closeAfter: Long) {
  private val executor = Executors.newSingleThreadExecutor()

  private val state = new AtomicReference[State.Value]
  private val failureCounter = new AtomicInteger
  private var lastFailureTime = new AtomicLong

  def execute[T](f: Callable[T]): T = {
    trip()

    if (state.get == State.OPEN)
      throw new RuntimeException("Unreachable: The circuit is in an open state")

    Try(executor.submit(f).get(timeout, TimeUnit.MILLISECONDS)) match {
      case Success(result) =>
        reset()
        result
      case Failure(exception) =>
        recordFailure()
        throw exception
    }
  }

  private def trip(): Unit = {
    if (failureCounter.get >= failureThreshold) {
      if (System.currentTimeMillis() - lastFailureTime.get() > closeAfter) {
        setState(State.HALF_OPEN)
      } else {
        setState(State.OPEN)
      }
    } else {
      setState(State.CLOSE)
    }
  }

  private def recordFailure(): Unit = {
    failureCounter.incrementAndGet()
    lastFailureTime.set(System.currentTimeMillis())
  }

  private def setState(state: State.Value): Unit = this.state.set(state)

  def reset(): Unit = {
    lastFailureTime = new AtomicLong
    failureCounter.set(0)

    setState(State.CLOSE)
  }

  def shutdown(): Unit = executor.shutdown()
}