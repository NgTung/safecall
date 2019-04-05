package org.safecall.circuitbreaker

import org.safecall.util.SimpleHttpClient
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CircuitBreakerSpec extends FlatSpec {

  "Circuit Breaker" should "return content of the web page as string" in {
    val targetWebpage = "https://postman-echo.com/post"
    val circuitBreaker = new CircuitBreaker(
      failureThreshold = 3, // 3 times
      timeout = 2000, // 2 second
      closeAfter = 1000 // 1 second
    )

    val result = Await.result(circuitBreaker.execute(() => {
      val formData = Map(
        "foo1" -> "bar"
      )
      SimpleHttpClient().post[String](targetWebpage, formData)
    }), Duration.Inf)

    circuitBreaker.shutdown()

    println(result)

    assert(result.isInstanceOf[String])
  }

}
