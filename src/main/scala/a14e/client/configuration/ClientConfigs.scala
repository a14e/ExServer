package a14e.client.configuration

import net.ceedubs.ficus.readers.ValueReader
import net.ceedubs.ficus.Ficus._

case class ClientConfigs(host: String,
                         port: Int)

object ClientConfigsReaders {
  implicit val clientConfigsReader: ValueReader[ClientConfigs] = ValueReader.relative { config =>
    ClientConfigs(
      host = config.as[String]("host"),
      port = config.as[Int]("port"),
    )
  }
}