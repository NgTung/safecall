package org.safecall.retryer

trait Constraint
trait Done extends Constraint
trait Required extends Constraint

class RetryBuilder[A <: Constraint, B <: Constraint] private() {
  private var policy: RetryPolicy = RetryPolicy.default()

  def withWaitStrategy(waitStrategy: WaitStrategy): RetryBuilder[Done, B] = {
    policy = policy.copy(waitStrategy = waitStrategy)

    this.asInstanceOf[RetryBuilder[Done, B]]
  }

  def withStopStrategy(stopStrategy: StopStrategy): RetryBuilder[A, Done] = {
    policy = policy.copy(stopStrategy = stopStrategy)

    this.asInstanceOf[RetryBuilder[A, Done]]
  }

  def retryIfResultNot(executeStrategy: ExecuteStrategy): RetryBuilder[A, B] = {
    policy = policy.copy(executeStrategy = executeStrategy)

    this.asInstanceOf[RetryBuilder[A, B]]
  }

  def build()(implicit ev: RetryBuilder[A, B] <:< RetryBuilder[Done, Done]): Retryable = new RetryerImpl(policy)
}

object RetryBuilder {

  def newBuilder() = new RetryBuilder[Required, Required]

}
