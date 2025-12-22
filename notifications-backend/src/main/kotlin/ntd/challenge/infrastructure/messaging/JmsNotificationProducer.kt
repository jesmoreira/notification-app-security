package ntd.challenge.infrastructure.messaging

import io.micronaut.jms.annotations.JMSProducer
import io.micronaut.jms.annotations.Queue
import io.micronaut.messaging.annotation.MessageBody
import jakarta.inject.Singleton
import ntd.challenge.application.dto.NotificationJob
import ntd.challenge.core.ports.NotificationProducerPort
import io.micronaut.serde.ObjectMapper
import org.slf4j.LoggerFactory

@JMSProducer("connectionFactory")
interface JmsClient {
    @Queue("\${notifications.queue.name}")
    fun send(@MessageBody body: String)
}

@Singleton
class SqsProducer(
    private val jmsClient: JmsClient,
    private val objectMapper: ObjectMapper
) : NotificationProducerPort {
    private val logger = LoggerFactory.getLogger(SqsProducer::class.java)

    override fun send(job: NotificationJob) {
        try {
            val json = objectMapper.writeValueAsString(job)
            logger.debug("Sending job to queue: logId=${job.logId}, type=${job.type}")
            jmsClient.send(json)
            logger.debug("Job sent successfully: logId=${job.logId}")
        } catch (e: Exception) {
            logger.error("Failed to send notification job to queue: logId=${job.logId}, error=${e.message}", e)
            throw e
        }
    }
}