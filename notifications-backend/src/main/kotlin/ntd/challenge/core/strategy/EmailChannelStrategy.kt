package ntd.challenge.core.strategy

import jakarta.inject.Singleton
import ntd.challenge.core.enums.ChannelType
import org.slf4j.LoggerFactory

@Singleton
class EmailStrategy : NotificationChannelStrategy {
    private val logger = LoggerFactory.getLogger(EmailStrategy::class.java)

    override fun supports(channel: ChannelType) = channel == ChannelType.EMAIL

    override fun send(destination: String, message: String) {
        logger.info("📧 Sending EMAIL to [$destination]: $message")
        Thread.sleep(50)
    }
}