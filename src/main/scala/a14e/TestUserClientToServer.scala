package a14e

import java.nio.ByteOrder

import a14e.client.protocol.DataDeserializer
import a14e.modules.{ConcurrentModule, ConfigurationModule}
import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Framing, Source, Tcp}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor
import scala.util.Failure

object TestUserClientToServer extends App
  with LazyLogging {

  implicit lazy val actorSystem: ActorSystem = ActorSystem()
  implicit lazy val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()

  val handling = Flow[ByteString]
    .via(
      Framing.delimiter(ByteString("\n"), maximumFrameLength = 1024, allowTruncation = true)
    )
    .map(_.utf8String)
    .map { data =>
      logger.info(s"received $data")
      data
    }
    .map(_ => ByteString.empty)


  Tcp().outgoingConnection("127.0.0.1", 9000)
    .join(handling)
    .run()
    .andThen {
      case Failure(e) =>
        logger.error("error", e.getMessage)
        e.printStackTrace()
    }
}
