package a14e.server.configuration

import net.ceedubs.ficus.readers.ValueReader
import net.ceedubs.ficus.Ficus._

case class ServerConfigs(port: Int,
                         sendBuffer: Int)

object ServerConfigsReaders {
  implicit val serverConfigsReader: ValueReader[ServerConfigs] = ValueReader.relative { config =>
    ServerConfigs(
      port = config.as[Int]("port"),
      sendBuffer = config.as[Int]("sending-buffer")
    )
  }
}