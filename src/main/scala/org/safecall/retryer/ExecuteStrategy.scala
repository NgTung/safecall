package org.safecall.retryer

import scala.util.Try

trait ExecuteStrategy {

  def run[T](fnc: => T): Try[T]

}

class ExceptionExecuteStrategy extends ExecuteStrategy {
  override def run[T](fnc: => T): Try[T] = Try(fnc)
}

class ResultCompareExecuteStrategy[U](compareFunc:U => Boolean) extends ExecuteStrategy {

  override def run[T](fnc: => T): Try[T] = Try {
    val result = fnc
    if(!compareFunc(result.asInstanceOf[U])) {
      throw new Exception()
    }
    result
  }

}