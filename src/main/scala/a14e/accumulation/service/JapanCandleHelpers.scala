package a14e.accumulation.service

import a14e.accumulation.model.{AccumulatedCandles, JapanCandle, SingleIntervalCandles}
import a14e.client.protocol.DataMessage

import scala.collection.immutable

object JapanCandleHelpers {
  def convertDataToCandles(data: immutable.Seq[DataMessage]): immutable.Seq[JapanCandle] = {
    data.groupBy(_.ticket)
      .map { case (ticket, messages) =>
        val sortedMessages = messages.sortBy(_.timestamp.getEpochSecond)

        val timestamp = sortedMessages.head.timestamp
        val open = sortedMessages.head.price
        val close = sortedMessages.last.price
        val hight = messages.map(_.price).max
        val low = messages.map(_.price).min
        val volume = messages.map(_.size).sum


        JapanCandle(
          ticket = ticket,
          timestamp = timestamp,
          open = open,
          close = close,
          high = hight,
          low = low,
          volume = volume
        )
      }.to[immutable.Seq]
  }

  def addCandlesToAccumulation(accumulated: AccumulatedCandles,
                               newCandles: immutable.Seq[JapanCandle],
                               historySize: Int): AccumulatedCandles = {

    val last = Some(SingleIntervalCandles(newCandles))
    val oldCandles = {
      val resultData = accumulated.lastMessage ++: accumulated.oldCandles

      if(resultData.length > historySize) resultData.take(historySize)
      else resultData
    }

    AccumulatedCandles(last, oldCandles)

  }

}