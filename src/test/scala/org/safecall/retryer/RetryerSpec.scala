package org.safecall.retryer

import java.util.concurrent.TimeUnit

import org.scalatest.FlatSpec
import play.shaded.ahc.org.asynchttpclient.{DefaultAsyncHttpClient, Response}

class RetryerSpec extends FlatSpec {

  "Retryer" should "return get request response" in {
    val httpClient = new DefaultAsyncHttpClient()

    val retryer = RetryBuilder.newBuilder
      .withWaitStrategy(ExponentialWait(delay = 500, factor = 1.5))
      .withStopStrategy(CountDownAttempt(times = 5))
      .retryIfResultNot(new ResultCompareExecuteStrategy[Response](res => res.getStatusCode == 200))
      .build()

    val result = retryer.execute({
      httpClient
        .prepareGet("https://www.google.com/")
        .execute()
        .get(3000, TimeUnit.MILLISECONDS)
    })

    assert(result.getStatusCode == 200)
  }

}
