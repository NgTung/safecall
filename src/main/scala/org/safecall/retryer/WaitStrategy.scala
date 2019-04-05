package org.safecall.retryer

import java.util.concurrent.atomic.AtomicLong
import scala.concurrent.duration._

trait WaitStrategy {

  def toMillis: Long

  def nextTick(): Unit

}

case class ExponentialWait(delay: Long, factor: Double) extends WaitStrategy {
  private val lastDelayTime: AtomicLong = new AtomicLong(delay)

  override def toMillis: Long = this.lastDelayTime.get()

  override def nextTick(): Unit = increaseDelayTime()

  private def increaseDelayTime(): Unit = {
    this.lastDelayTime.set((this.lastDelayTime.get() * factor).toLong)
  }

}

case class RandomWaitFor(time: Duration = 2 minute) extends WaitStrategy {
  private val lastDelayTime: AtomicLong = new AtomicLong(nextLong(1, time.toMillis))

  override def toMillis: Long = this.lastDelayTime.get()

  override def nextTick(): Unit = this.lastDelayTime.set(nextLong(1, time.toMillis))

  private def nextLong(min: Long, max: Long): Long = min + (Math.random() * (max - min)).toLong

}