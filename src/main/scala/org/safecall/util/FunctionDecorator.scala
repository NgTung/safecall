package org.safecall.util

import java.util.concurrent.Callable

object FunctionDecorator {

  implicit class FunctionDecorator[T](function: => T) {
    def toCallable: Callable[T] = () => function
  }

}
