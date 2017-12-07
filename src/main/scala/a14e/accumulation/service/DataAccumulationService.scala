package a14e.accumulation.service

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import java.util.function.UnaryOperator

import a14e.accumulation.configuration.DataAccumulationConfig
import a14e.accumulation.model.{AccumulatedCandles, JapanCandle}
import a14e.client.protocol.DataMessage
import a14e.client.service.DataClient
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source, SourceQueue}
import akka.stream.{Materializer, OverflowStrategy}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.concurrent.TrieMap
import scala.collection.immutable
import scala.concurrent.ExecutionContext
import scala.util.Try

trait DataAccumulationService {

  def generateDataAccumulationFlow(): Flow[DataMessage, AccumulatedCandles, NotUsed]

}


class DataAccumulationServiceImpl(configs: DataAccumulationConfig,
                                  dataClient: DataClient)
                                 (implicit
                                  context: ExecutionContext,
                                  actorSystem: ActorSystem,
                                  materializer: Materializer) extends DataAccumulationService with LazyLogging {


  def generateDataAccumulationFlow(): Flow[DataMessage, AccumulatedCandles, NotUsed] = {
    Flow[DataMessage]
      .groupedWithin(configs.bufferSize, configs.candleTimeout)
      .map(JapanCandleHelpers.convertDataToCandles)
      .map { candles =>
        logger.info(s"accumulated candles $candles")
        candles
      }
      .scan(AccumulatedCandles.empty)(JapanCandleHelpers.addCandlesToAccumulation(_, _, configs.historySize))
  }

}