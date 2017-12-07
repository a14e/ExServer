package a14e.client.protocol

import java.nio.charset.StandardCharsets
import java.time.Instant

import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}


case class DataMessage(timestamp: Instant,
                       ticket: String,
                       price: Double,
                       size: Int)


