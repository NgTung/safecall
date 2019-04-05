Safecall
=================================

A lightweight library help to making your API more resilient with code that tracks the result of each external resources call

## Get started
Requisite installation:
- Java OpenJDK 1.8+
- Scala 2.12.+
- Sbt

You should add the following dependency:
```
// TODO
```

## How to use
- With Retry pattern:
```scala
object Application {
  import java.util.concurrent.TimeUnit
  import play.shaded.ahc.org.asynchttpclient.{DefaultAsyncHttpClient, Response}
  
  def getAPIResponse(url: String, timeout: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): Response = {
    val httpClient = new DefaultAsyncHttpClient()
    httpClient
      .prepareGet(url)
      .execute()
      .get(timeout, timeUnit)
  }
  
  def main(args: Array[String]): Unit = {
    val retryer = RetryBuilder.newBuilder
      .withWaitStrategy(ExponentialWait(delay = 500, factor = 1.5))
      .withStopStrategy(CountDownAttempt(times = 5))
      .retryIfResultNot(new ResultCompareExecuteStrategy[Response](res => res.getStatusCode == 200))
      .build()
  
    val result = retryer.execute(getAPIResponse("https://www.google.com/", timeout = 3000))
  
    println(result)
  }
}

```

- With Circuit Breaker pattern:
```scala
val targetWebpage = "http://www.google.com"
val circuitBreaker = new CircuitBreaker(
  failureThreshold = 3, // 3 times
  timeout = 2000, // 2 second
  closeAfter = 1000 // 1 second
)

val result = Await.result(circuitBreaker.execute(() => {
  HttpClient().get[String](targetWebpage)
}), Duration.Inf)

circuitBreaker.shutdown()
```

## Testing

```bash
$ sbt test
```