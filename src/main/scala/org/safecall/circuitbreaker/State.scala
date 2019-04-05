package org.safecall.circuitbreaker

object State extends Enumeration {
  type State = Value

  val CLOSE, OPEN, HALF_OPEN = Value
}
