package a14e.accumulation.model

import java.time.Instant

import a14e.client.protocol.DataMessage
import scala.collection.immutable

case class JapanCandle(ticket: String,
                       timestamp: Instant,
                       open: Double,
                       high: Double,
                       low: Double,
                       close: Double,
                       volume: Double)

case class SingleIntervalCandles(data: immutable.Seq[JapanCandle])

case class AccumulatedCandles(lastMessage: Option[SingleIntervalCandles],
                              oldCandles: Vector[SingleIntervalCandles])

object AccumulatedCandles {
  def empty = AccumulatedCandles(None, Vector.empty)
}


