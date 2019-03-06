package br.com.kurtis.payment_details;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class PaymentMessageCodec implements MessageCodec<Payment, Payment> {

  @Override
  public void encodeToWire(final Buffer buffer, final Payment payment) {
    final String string = Json.encode(payment);
    final int length = string.getBytes().length;
    buffer.appendInt(length);
    buffer.appendString(string);
  }

  @Override
  public Payment decodeFromWire(final int position, final Buffer buffer) {
    final int length = buffer.getInt(position);
    final int start = position + 4;
    final int end = start + length;

    final String string = buffer.getString(start, end);
    return Json.decodeValue(string, Payment.class);
  }

  @Override
  public Payment transform(final Payment payment) {
    return payment;
  }

  @Override
  public String name() {
    return "payment";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
