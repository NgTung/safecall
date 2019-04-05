package org.safecall.util

import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient

import scala.concurrent.Future

trait SimpleHttpClient {

  def get[T](url: String): Future[T]

  def post[T](url: String, formData: Map[String, String]): Future[T]

}

object SimpleHttpClient {
  object ClientInstance extends Enumeration {
    type State = Value

    val DEFAULT, PLAY_WSCLIENT = Value
  }

  private class PlayWebserviceRequest extends SimpleHttpClient {
    import akka.actor.ActorSystem
    import akka.stream.ActorMaterializer
    import play.api.libs.ws.ahc.StandaloneAhcWSClient
    import scala.concurrent.ExecutionContext.Implicits._

    lazy val WSClient: StandaloneAhcWSClient = {
      implicit val system: ActorSystem = ActorSystem()
      implicit val materializer: ActorMaterializer = ActorMaterializer()
      val client = StandaloneAhcWSClient()

      system.registerOnTermination {
        client.close()
        System.exit(0)
      }

      client
    }

    override def get[T](url: String): Future[T] = WSClient.url(url).get().map(_.body.asInstanceOf[T])

    override def post[T](url: String, formData: Map[String, String]): Future[T] = {
      import play.shaded.ahc.org.asynchttpclient.request.body.multipart._

      val client = WSClient.underlying.asInstanceOf[AsyncHttpClient]
      val builder = client.preparePost(url)
      builder.setHeader("Content-Type", "multipart/form-data")
      formData.foreach(data => builder.addBodyPart(new StringPart(data._1, data._2)))

      Future.apply(client.executeRequest(builder.build()).get().getResponseBody.asInstanceOf[T])
    }
  }

  def apply(clientInstance: ClientInstance.Value = ClientInstance.DEFAULT): SimpleHttpClient = clientInstance match {
    case _ @ (ClientInstance.DEFAULT | ClientInstance.PLAY_WSCLIENT) => new PlayWebserviceRequest
    case _ => new PlayWebserviceRequest
  }

}