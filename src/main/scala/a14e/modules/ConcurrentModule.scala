package a14e.modules

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

trait ConcurrentModule {
  this: ConfigurationModule =>

  implicit lazy val actorSystem: ActorSystem = ActorSystem("main-system",configuration)
  implicit lazy val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()
}
