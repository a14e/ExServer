package a14e.client.protocol

import java.nio.charset.StandardCharsets
import java.time.Instant

import akka.util.ByteString

object DataDeserializer {

  /**
    *  Читаем обьект из байтов
    *
    * [ LEN:2 ] [ TIMESTAMP:8 ] [ TICKER_LEN:2 ] [ TICKER:TICKER_LEN ] [ PRICE:8 ] [ SIZE:4 ]
    *
    * LEN: длина последующего сообщения (целое, 2 байта)
    * TIMESTAMP: дата и время события (целое, 8 байт, milliseconds since epoch)
    * TICKER_LEN: длина биржевого тикера (целое, 2 байта)
    * TICKER: биржевой тикер (ASCII, TICKER_LEN байт)
    * PRICE: цена сделки (double, 8 байт)
    * SIZE: объем сделки (целое, 4 байта)
    * */
  def read(data: ByteString): DataMessage =  {
    val ticketInitIndex = 12
    val headerLen = 2

    val buffer = data.toByteBuffer

    buffer.position(headerLen)
    val timeLong = buffer.getLong
    val time = Instant.ofEpochMilli(timeLong)
    val tiketLen = buffer.getShort
    val ticketEndIndex = ticketInitIndex + tiketLen

    val ticketBytes = data.slice(ticketInitIndex, ticketEndIndex)
    val ticket = ticketBytes.decodeString(StandardCharsets.US_ASCII)

    buffer.position(ticketEndIndex)
    val price = buffer.getDouble
    val size = buffer.getInt

    DataMessage(
      timestamp = time,
      ticket = ticket,
      price = price,
      size = size
    )
  }


}