package a14e.modules

import a14e.accumulation.service.DataAccumulationServiceImpl
import a14e.client.service.{DataClient, DataClientImpl}
import a14e.server.Server
import a14e.server.ServerImpl
import com.softwaremill.macwire._

trait ServicesModule {
  this: ConfigurationModule
    with ConcurrentModule =>

  lazy val serverImpl: Server = wire[ServerImpl]
  lazy val dataClientImpl: DataClient = wire[DataClientImpl]
  lazy val dataAccumulationServiceImpl: DataAccumulationServiceImpl = wire[DataAccumulationServiceImpl]
}
