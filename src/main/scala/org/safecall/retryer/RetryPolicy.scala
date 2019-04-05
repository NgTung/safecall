package org.safecall.retryer

case class RetryPolicy(
  waitStrategy: WaitStrategy,
  stopStrategy: StopStrategy,
  executeStrategy: ExecuteStrategy = new ExceptionExecuteStrategy()
)

object RetryPolicy {

  def default(): RetryPolicy = RetryPolicy(
    ExponentialWait(delay = 1000, factor = 1.5), // delay 1s with exponential wait factor is 1.5
    CountDownAttempt(times = 3) // retry 3 times
  )

}