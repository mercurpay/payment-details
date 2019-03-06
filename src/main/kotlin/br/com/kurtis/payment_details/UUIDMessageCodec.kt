package br.com.kurtis.payment_details

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import java.util.*

class UUIDMessageCodec : MessageCodec<UUID, UUID> {

    override fun encodeToWire(buffer: Buffer, uuid: UUID) {
        val string = uuid.toString()
        val length = string.toByteArray().size
        buffer.appendInt(length)
        buffer.appendString(string)
    }

    override fun decodeFromWire(position: Int, buffer: Buffer): UUID {
        val length = buffer.getInt(position)
        val start = position + 4
        val end = start + length

        val string = buffer.getString(start, end)
        return UUID.fromString(string)
    }

    override fun transform(uuid: UUID): UUID {
        return uuid
    }

    override fun name(): String {
        return "uuid"
    }

    override fun systemCodecID(): Byte {
        return -1
    }
}
