package ntd.challenge.core.strategy

import jakarta.inject.Singleton
import ntd.challenge.core.enums.ChannelType
import org.slf4j.LoggerFactory

@Singleton
class PushChannelStrategy : NotificationChannelStrategy {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun supports(channel: ChannelType) = channel == ChannelType.PUSH_NOTIFICATION

    override fun send(destination: String, message: String) {
        Thread.sleep(50)
        logger.info("[PUSH] Enviado para token $destination: \"$message\"")
    }
}