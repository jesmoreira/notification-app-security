package ntd.challenge.core.service

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import ntd.challenge.application.dto.NotificationJob
import ntd.challenge.application.dto.NotificationLogResponse
import ntd.challenge.application.dto.SendNotificationRequest
import ntd.challenge.core.enums.Category
import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.model.NotificationLog
import ntd.challenge.core.ports.NotificationProducerPort
import ntd.challenge.core.ports.NotificationServicePort
import ntd.challenge.core.repository.NotificationLogRepository
import ntd.challenge.core.repository.UserRepository
import org.slf4j.LoggerFactory
import java.util.UUID

@Singleton
open class NotificationService(
    private val userRepository: UserRepository,
    private val logRepository: NotificationLogRepository,
    private val producer: NotificationProducerPort
) : NotificationServicePort {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    override fun submitNotification(request: SendNotificationRequest): UUID {
        val trackingId = UUID.randomUUID()
        try {
            val jobsToSend = persistLogsAndCreateJobs(request)

            jobsToSend.forEach { job ->
                try {
                    producer.send(job)
                } catch (e: Exception) {
                    logger.error("Failed to send job to queue: ${job.logId}", e)
                }
            }
        } catch (e: Exception) {
            logger.error("Error in notification submission", e)
            throw e
        }

        return trackingId
    }

    @Transactional
    open fun persistLogsAndCreateJobs(request: SendNotificationRequest): List<NotificationJob> {
        val categoryEnum = try {
            Category.valueOf(request.category.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid category: ${request.category}")
        }

        val users = userRepository.findByCategoryWithChannels(categoryEnum)
        val jobs = mutableListOf<NotificationJob>()

        if (users.isEmpty()) {
            return emptyList()
        }

        users.forEach { user ->
            val channels = user.channels.ifEmpty { setOf(ChannelType.EMAIL) }

            channels.forEach { channelType ->
                val log = NotificationLog(
                    user = user,
                    category = categoryEnum,
                    channel = channelType,
                    messageBody = request.message,
                    status = "PENDING"
                )

                val savedLog = logRepository.save(log)

                val destination = when (channelType) {
                    ChannelType.SMS -> user.phoneNumber
                    ChannelType.EMAIL -> user.email
                    ChannelType.PUSH_NOTIFICATION -> user.id.toString()
                }

                jobs.add(
                    NotificationJob(
                        logId = savedLog.id!!,
                        userId = user.id!!,
                        userName = user.name,
                        destination = destination,
                        type = channelType.name,
                        message = request.message
                    )
                )
            }
        }
        return jobs
    }

    override fun findLogs(search: String, pageable: Pageable): Page<NotificationLogResponse> {
        val entityPage = logRepository.searchLogs(search, pageable)

        return entityPage.map { log ->
            NotificationLogResponse(
                id = log.id!!,
                userName = log.user.name,
                category = log.category.name,
                channel = log.channel.name,
                message = log.messageBody,
                status = log.status,
                timestamp = log.createdAt
            )
        }
    }
}