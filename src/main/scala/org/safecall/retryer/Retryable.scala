package org.safecall.retryer

trait Retryable {

  def execute[T](fnc: => T): T

}
