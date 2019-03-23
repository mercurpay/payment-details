package br.com.kurtis.payment_details

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.Timeout
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.IOException
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
class PaymentProducerVerticleTest {

    private var port: Int = 0
    private val log = LoggerFactory.getLogger(PaymentProducerVerticleTest::class.java)

    @BeforeEach
    @Throws(IOException::class)
    internal fun deployVerticle(vertx: Vertx, context: VertxTestContext) {
        ServerSocket(0).use { this.port = it.localPort }
        log.info("Running tests on port ${this.port}")
        val options = DeploymentOptions().setConfig(JsonObject().put("http.port", this.port))
        vertx.deployVerticle(PaymentProducerVerticle(), options, context.succeeding { context.completeNow() })
    }

    @Test
    @DisplayName("Should start a Web Server")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    internal fun testHttpServer(vertx: Vertx, context: VertxTestContext) {
        WebClient.create(vertx)
                .get(this.port, "localhost", "/payments/${UUID.randomUUID()}")
                .`as`(BodyCodec.string())
                .send {
                    assertThat(it.succeeded()).isTrue()
                    log.info("Server receive successfully the request")
                    val response = it.result()
                    assertThat(response.statusCode()).isEqualTo(200)
                    log.info("The response contains the expected body")
                    context.completeNow()
                }
    }
}
