package a14e.client.service

import java.nio.ByteOrder

import a14e.client.configuration.ClientConfigs
import a14e.client.protocol.{DataDeserializer, DataMessage}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Framing, Keep, RunnableGraph, Sink, Source, Tcp}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import org.reactivestreams.{Publisher, Subscriber}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}


trait DataClient {
  def bindAndGetSource(): Source[DataMessage, NotUsed]
}


class DataClientImpl(clientConfigs: ClientConfigs)
                    (implicit
                     context: ExecutionContext,
                     system: ActorSystem,
                     materialize: Materializer) extends DataClient with LazyLogging {

  def bindAndGetSource(): Source[DataMessage, NotUsed] = {

    logger.info(s"initializing connection to ${clientConfigs.host}: ${clientConfigs.port}")

    val (subscriber, publisher) = Source.asSubscriber[ByteString].via(
      Framing.lengthField(
        fieldLength = 2,
        fieldOffset = 0,
        maximumFrameLength = 1024,
        byteOrder = ByteOrder.BIG_ENDIAN
      )
    )
      .map(DataDeserializer.read)
      .map { data =>
        logger.info(s"received $data")
        data
      }.toMat(Sink.asPublisher(true))(Keep.both)
      .run()

    val flow = Flow.fromSinkAndSource(
      Sink.fromSubscriber(subscriber),
      Source.single(ByteString.empty) // Handshake
    )

    Tcp().outgoingConnection(clientConfigs.host, clientConfigs.port)
      .join(flow)
      .run()
      .andThen {
        case Success(c) =>
          logger.info(s"connected to $c")
        case Failure(e) =>
          logger.error("connection failed with error", e)
      }


     Source.fromPublisher(publisher)
  }


}