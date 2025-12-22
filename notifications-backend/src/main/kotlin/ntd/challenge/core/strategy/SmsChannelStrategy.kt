package ntd.challenge.core.strategy

import jakarta.inject.Singleton
import ntd.challenge.core.enums.ChannelType
import org.slf4j.LoggerFactory

@Singleton
class SmsStrategy : NotificationChannelStrategy {
    private val logger = LoggerFactory.getLogger(SmsStrategy::class.java)

    override fun supports(channel: ChannelType) = channel == ChannelType.SMS

    override fun send(destination: String, message: String) {
        logger.info("📱 Sending SMS to [$destination]: $message")
        Thread.sleep(50)
    }
}