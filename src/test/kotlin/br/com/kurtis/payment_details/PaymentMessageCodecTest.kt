package br.com.kurtis.payment_details

import io.vertx.core.buffer.Buffer
import io.vertx.core.json.Json
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

@DisplayName("Given a Codec of Payment Message")
class PaymentMessageCodecTest {

    @Nested
    @DisplayName("When encode a Payment")
    internal inner class WhenEncodeAPayment {

        @Test
        @DisplayName("Then it should decode this value properly")
        fun thenItShouldDecodeThisValueProperly() {
            val codec = PaymentMessageCodec()
            val buff = Buffer.buffer()

            val amount = Amount("$", BigDecimal.valueOf(356.666))
            val games = Payment(UUID.randomUUID(), "Games for Xbox", amount)
            codec.encodeToWire(buff, games)

            val position = 0
            val length = buff.getInt(position)
            val start = position + 4
            val end = start + length

            val string = buff.getString(start, end)
            val payment = Json.decodeValue(string, Payment::class.java)
            assertThat(payment).isNotNull()
        }
    }
}
