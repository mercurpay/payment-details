package br.com.kurtis.payment_details

import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import java.util.*

class PaymentProducerVerticle : AbstractVerticle() {

    companion object {
        private const val API_PATH = "/payments"
        private const val DEFAULT_PORT = 8080
    }

    private val log = LoggerFactory.getLogger(PaymentProducerVerticle::class.java)

    override fun init(vertx: Vertx?, context: Context?) {
        super.init(vertx, context)
        vertx?.deployVerticle(PaymentConsumerVerticle::class.java.name)
        val eventBus = vertx?.eventBus()
        eventBus?.registerDefaultCodec(UUID::class.java, UUIDMessageCodec())
        eventBus?.registerDefaultCodec(Payment::class.java, PaymentMessageCodec())
    }

    override fun start(bootstrap: Future<Void>) {
        val router = Router.router(this.vertx)
        router.get("$API_PATH/:id").handler { this.findById(it) }
        router.get(API_PATH).handler { this.findAll(it) }
        router.post(API_PATH).handler { this.create(it) }
        router.put("$API_PATH/:id").handler { this.update(it) }
        router.patch("$API_PATH/:id").handler { this.update(it) }
        router.delete("$API_PATH/:id").handler { this.deleteById(it) }

        val httpPort = this.config().getInteger("http.port", DEFAULT_PORT)
        this.vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(httpPort)
                .doOnSuccess { bootstrap.complete() }
                .doOnError { bootstrap.fail(it.cause) }
                .subscribe()
    }

    private fun create(routing: RoutingContext) {
        routing.response().setStatusCode(405).end()
    }

    private fun deleteById(routing: RoutingContext) {
        routing.response().setStatusCode(405).end()
    }

    private fun findAll(routing: RoutingContext) {
        routing.response().setStatusCode(405).end()
    }

    private fun findById(routing: RoutingContext) {
        val traceId = UUID.randomUUID().toString()
        try {
            val id = routing.request().getParam("id")!!
            log.info("[$traceId] Message sent")
            val deliveryOptions = DeliveryOptions().addHeader("traceId", traceId)
            this.vertx.eventBus()
                    .rxSend<Payment>(PaymentConsumerVerticle.ADDRESS, UUID.fromString(id), deliveryOptions)
                    .map { it.body() }
                    .map { Json.encodePrettily(it) }
                    .doOnSuccess {
                        routing.response()
                                .putHeader("Content-Type", "application/json;charset=UTF-8")
                                .setStatusCode(200)
                                .end(it)
                    }
                    .doOnError {
                        log.error("[$traceId] Failed to get payment", it)
                        routing.response()
                                .putHeader("Content-Type", "application/json;charset=UTF-8")
                                .setStatusCode(500)
                                .end(JsonObject().put("message", it.message).encodePrettily())
                    }
                    .subscribe()
        } catch (e: Exception) {
            log.error("[$traceId] Failed to get payment", e)
            throw e
        }
    }

    private fun update(routing: RoutingContext) {
        routing.response().setStatusCode(405).end()
    }

}
