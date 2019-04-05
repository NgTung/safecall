package org.safecall.retryer

import java.util.concurrent.{Callable, Executors}
import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.duration.TimeUnit

trait StopStrategy {

  def attempt(): Boolean

  def scheduledExecute[T](func: => Callable[T], delay: Long, timeUnit: TimeUnit): T

}

case class CountDownAttempt(times: Int) extends StopStrategy {
  private val countDownAttempt = new AtomicInteger(times)
  private val executor = Executors.newScheduledThreadPool(times + 1)

  override def attempt(): Boolean = {
    val tryAgain: Boolean = countDownAttempt.decrementAndGet() >= 0
    if (!tryAgain)
      executor.shutdown()
    tryAgain
  }

  override def scheduledExecute[T](func: => Callable[T], delay: Long, timeUnit: TimeUnit): T = {
    executor.schedule(func, delay, timeUnit).get()
  }
}
