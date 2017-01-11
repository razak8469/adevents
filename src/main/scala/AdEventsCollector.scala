package adevents

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import akka.stream.ActorMaterializer
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import spray.json._

  import scala.io.StdIn
  import AdEvents._
  import com.typesafe.config.ConfigFactory

  object AdEventsCollector {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("collector")
    implicit val system = ActorSystem("collector", config)
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val publisher = system.actorOf(AdEventsPublisher.props)

    val route =
      path("adevents"){
      post{
        entity(as[JsValue]) {json =>
          val event = json.asJsObject.fields.getOrElse("name", JsNull)
          event match {
            case JsString("adServed") =>
              publisher ! json.convertTo[AdServe]
              complete(StatusCodes.OK)
            case JsString("urlOpened") =>
              publisher ! json.convertTo[UrlOpen]
              complete(StatusCodes.OK)
            case JsString("videoSegmentViewed") =>
              publisher ! json.convertTo[VideoView]
              complete(StatusCodes.OK)
            case _ =>
              complete(StatusCodes.NotAcceptable)
          }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)
    println(s"Server online at http://localhost:8080\n" +
      s"Press RETURN to stop....")

    StdIn.readLine
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
    System.exit(0)
  }
}