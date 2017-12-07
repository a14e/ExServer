package a14e

import java.io.File

import a14e.modules.{ConcurrentModule, ConfigurationModule, ServicesModule}
import com.typesafe.config.ConfigFactory


object ExServer extends App
  with ServicesModule
  with ConfigurationModule
  with ConcurrentModule {
  val configuration = ConfigFactory.parseFile(new File("./conf/application.conf"))

  serverImpl.init()
}
