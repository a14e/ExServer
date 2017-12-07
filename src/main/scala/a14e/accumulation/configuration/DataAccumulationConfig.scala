package a14e.accumulation.configuration

import scala.concurrent.duration.FiniteDuration

import net.ceedubs.ficus.readers.ValueReader
import net.ceedubs.ficus.Ficus._


case class DataAccumulationConfig(bufferSize: Int,
                                  candleTimeout: FiniteDuration,
                                  historySize: Int)


object DataAccumulationConfigReaders {
  implicit val dataAccumulationConfigReader: ValueReader[DataAccumulationConfig] =
    ValueReader.relative { config =>
      DataAccumulationConfig(
        bufferSize = config.as[Int]("buffer-size"),
        candleTimeout = config.as[FiniteDuration]("candle-timeout"),
        historySize = config.as[Int]("history-size")
      )
    }
}