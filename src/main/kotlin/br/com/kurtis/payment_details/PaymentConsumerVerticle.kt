package br.com.kurtis.payment_details

import io.vertx.core.Future
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.AbstractVerticle
import java.math.BigDecimal
import java.util.*

class PaymentConsumerVerticle : AbstractVerticle() {

    companion object {
        const val ADDRESS = "payments-search"
    }

    private val log = LoggerFactory.getLogger(PaymentConsumerVerticle::class.java)

    override fun start(startFuture: Future<Void>) {
        this.vertx.eventBus().consumer<UUID>(ADDRESS).handler {
            val traceId = it.headers().get("traceId")
            val id = it.body()
            log.info("[$traceId] Received message to search for Payment $id")
            val amount = Amount("R$", BigDecimal.valueOf(503.656))
            val payment = Payment(id, "Playstation Games", amount)
            it.reply(payment)
        }
    }
}
