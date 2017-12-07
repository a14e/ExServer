package a14e.server

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import a14e.accumulation.model.{AccumulatedCandles, JapanCandle, SingleIntervalCandles}
import a14e.accumulation.service.DataAccumulationService
import a14e.client.service.DataClient
import a14e.server.configuration.ServerConfigs
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source, SourceQueueWithComplete, Tcp}
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import io.circe.java8.time._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.reactivestreams.Publisher
import scala.collection.immutable
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

trait Server {
  def init(): Unit
}


class ServerImpl(configs: ServerConfigs,
                 dataAccumulationService: DataAccumulationService,
                 client: DataClient)
                (implicit
                 context: ExecutionContext,
                 system: ActorSystem,
                 materialize: Materializer) extends Server with LazyLogging {

  override def init(): Unit = {
    logger.info(s"binding server to 127.0.0.1:${configs.port}")

    val incomingConnections: Source[IncomingConnection, Future[ServerBinding]] = Tcp().bind("127.0.0.1", configs.port)

    val lastPublishData = new AtomicReference[Option[AccumulatedCandles]](None)

    val publisher: Publisher[SingleIntervalCandles] =
      client.bindAndGetSource()
        .via(dataAccumulationService.generateDataAccumulationFlow())
        .mapConcat { data =>
          lastPublishData.set(Some(data))
          data.lastMessage.to[immutable.Seq]
        }
        .runWith(Sink.asPublisher(true))

    Source.fromPublisher(publisher).runWith(Sink.ignore) // preventing publisher death and overflow when 0 clients

    handleConnections(incomingConnections, publisher, lastPublishData)
  }

  private def handleConnections(incomingConnections: Source[IncomingConnection, Future[ServerBinding]],
                                publisher: Publisher[SingleIntervalCandles],
                                lastPublishData: AtomicReference[Option[AccumulatedCandles]]): Unit = {
    incomingConnections
      .runForeach { connection =>

        logger.info(s"incoming ${connection.remoteAddress}")

        val initData =
          Source(lastPublishData.get().to[immutable.Seq].flatMap(_.oldCandles))

        val sendingSource =
          Source.fromPublisher(publisher)
            .prepend(initData)
            .mapConcat(_.data)
            .map { candle =>
              val resultJson = candle.asJson
              val resultString = resultJson.noSpaces

              logger.info(s"sending $resultString to ${connection.remoteAddress}")
              resultString + '\n'
            }.map(ByteString(_))


        val connectionFlow = Flow.fromSinkAndSource(Sink.ignore, sendingSource)

        connection.handleWith(connectionFlow)

      }.andThen {
      case Failure(e) => logger.error("error", e)
    }
  }

}
