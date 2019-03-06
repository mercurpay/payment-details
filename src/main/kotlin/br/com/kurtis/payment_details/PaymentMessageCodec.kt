package br.com.kurtis.payment_details

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.Json

class PaymentMessageCodec : MessageCodec<Payment, Payment> {

  override fun encodeToWire(buffer: Buffer, payment: Payment) {
    val string = Json.encode(payment)
    val length = string.toByteArray().size
    buffer.appendInt(length)
    buffer.appendString(string)
  }

  override fun decodeFromWire(position: Int, buffer: Buffer): Payment {
    val length = buffer.getInt(position)
    val start = position + 4
    val end = start + length

    val string = buffer.getString(start, end)
    return Json.decodeValue(string, Payment::class.java)
  }

  override fun transform(payment: Payment): Payment {
    return payment
  }

  override fun name(): String {
    return "payment"
  }

  override fun systemCodecID(): Byte {
    return -1
  }
}
