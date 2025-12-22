package ntd.challenge.infrastructure.messaging

import io.micronaut.jms.annotations.JMSListener
import io.micronaut.jms.annotations.Queue
import io.micronaut.messaging.annotation.MessageBody
import jakarta.transaction.Transactional
import ntd.challenge.application.dto.NotificationJob
import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.model.NotificationLog
import ntd.challenge.core.repository.NotificationLogRepository
import ntd.challenge.core.strategy.NotificationChannelStrategy
import org.slf4j.LoggerFactory
import io.micronaut.serde.ObjectMapper

@JMSListener("connectionFactory")
open class NotificationWorker(
    private val strategies: List<NotificationChannelStrategy>,
    private val logRepository: NotificationLogRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(NotificationWorker::class.java)

    @Queue(value = "\${notifications.queue.name}")
    @Transactional
    open fun receive(@MessageBody body: String) {
        val job = objectMapper.readValue(body, NotificationJob::class.java)
        logger.info("⚡ [JMS] Processing Job: LogID=${job.logId}, Channel=${job.type}")

        try {
            val channelType = ChannelType.valueOf(job.type)

            val strategy = strategies.find { it.supports(channelType) }
                ?: throw IllegalArgumentException("No strategy found for channel: ${job.type}")

            strategy.send(job.destination, job.message)

            updateLogStatus(job, success = true)

        } catch (e: Exception) {
            logger.error("Error processing notification job ${job.logId}", e)
            updateLogStatus(job, success = false, errorMessage = e.message)
        }
    }

    private fun updateLogStatus(
        job: NotificationJob,
        success: Boolean,
        errorMessage: String? = null
    ) {
        val log = logRepository.findById(job.logId)
            .orElseThrow {
                IllegalStateException("Log integrity error: LogID ${job.logId} not found.")
            }

        val updatedLog = log.copy(
            status = if (success) "SUCCESS" else "ERROR",
            errorMessage = errorMessage
        )
        logRepository.update(updatedLog)
    }
}