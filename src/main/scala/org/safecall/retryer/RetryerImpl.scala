package org.safecall.retryer

import java.util.concurrent.TimeUnit

import scala.util.{Failure, Success}
import org.safecall.util.FunctionDecorator._

class RetryerImpl(retryPolicy: RetryPolicy) extends Retryable {
  private val waiter = retryPolicy.waitStrategy
  private val stopStrategy = retryPolicy.stopStrategy

  def execute[T](fnc: => T): T = retryPolicy.executeStrategy.run(fnc) match {
    case Success(result) => onSuccess(result)
    case Failure(exception) => onFailedAttempt(exception, fnc)
  }

  private def onSuccess[T](result: T): T = {
    result
  }

  private def onFailedAttempt[T](exception: Throwable, callback: => T): T = {
    if (!stopStrategy.attempt())
      onFailure(exception)

    waiter.nextTick()

    stopStrategy.scheduledExecute(execute(callback).toCallable, waiter.toMillis, TimeUnit.MILLISECONDS)
  }

  private def onFailure(exception: Throwable): Unit = {
    throw exception
  }

}