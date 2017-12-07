package a14e.modules

import a14e.accumulation.configuration.DataAccumulationConfig
import a14e.client.configuration.ClientConfigs
import a14e.server.configuration.ServerConfigs
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import a14e.server.configuration.ServerConfigsReaders._
import a14e.accumulation.configuration.DataAccumulationConfigReaders._
import a14e.client.configuration.ClientConfigsReaders._

trait ConfigurationModule {

  def configuration: Config


  lazy val serverConfigs: ServerConfigs = configuration.as[ServerConfigs]("exserver.server")
  lazy val clientConfigs: ClientConfigs = configuration.as[ClientConfigs]("exserver.client")

  lazy val dataAccumulationConfig: DataAccumulationConfig =
    configuration.as[DataAccumulationConfig]("exserver.data-accumulation")
}
