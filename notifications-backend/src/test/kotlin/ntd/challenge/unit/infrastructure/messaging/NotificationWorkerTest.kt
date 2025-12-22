package ntd.challenge.unit.infrastructure.messaging

import io.micronaut.serde.ObjectMapper
import ntd.challenge.application.dto.NotificationJob
import ntd.challenge.core.enums.Category
import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.model.NotificationLog
import ntd.challenge.core.model.User
import ntd.challenge.core.repository.NotificationLogRepository
import ntd.challenge.core.strategy.NotificationChannelStrategy
import ntd.challenge.infrastructure.messaging.NotificationWorker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
class NotificationWorkerTest {

    @Mock
    lateinit var logRepository: NotificationLogRepository

    @Mock
    lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var emailStrategy: NotificationChannelStrategy

    lateinit var worker: NotificationWorker

    @BeforeEach
    fun setup() {
        val strategies = listOf(emailStrategy)
        worker = NotificationWorker(strategies, logRepository, objectMapper)
    }

    @Test
    fun `receive should process message and update log status to SUCCESS`() {
        val job = NotificationJob(
            logId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            userName = "User",
            destination = "test@mail.com",
            type = "EMAIL",
            message = "Msg"
        )
        val json = "{}"

        val log = NotificationLog(
            id = job.logId,
            user = User(name = "User", email = "test@mail.com", phoneNumber = "1", channels = emptySet()),
            category = Category.FINANCE,
            channel = ChannelType.EMAIL,
            messageBody = "Msg",
            status = "PENDING"
        )
        whenever(objectMapper.readValue(eq(json), eq(NotificationJob::class.java))).thenReturn(job)
        whenever(emailStrategy.supports(ChannelType.EMAIL)).thenReturn(true)
        whenever(logRepository.findById(job.logId)).thenReturn(Optional.of(log))

        worker.receive(json)

        verify(emailStrategy).send(job.destination, job.message)

        verify(logRepository).update(check {
            assert(it.status == "SUCCESS") { "Status should be SUCCESS" }
        })
    }

    @Test
    fun `receive should update log status to ERROR on exception`() {
        val job = NotificationJob(
            logId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            userName = "User",
            destination = "test@mail.com",
            type = "EMAIL",
            message = "Msg"
        )
        val json = "{}"

        val log = NotificationLog(
            id = job.logId,
            user = User(name = "User", email = "test@mail.com", phoneNumber = "1", channels = emptySet()),
            category = Category.FINANCE,
            channel = ChannelType.EMAIL,
            messageBody = "Msg",
            status = "PENDING"
        )

        whenever(objectMapper.readValue(eq(json), eq(NotificationJob::class.java))).thenReturn(job)
        whenever(emailStrategy.supports(ChannelType.EMAIL)).thenReturn(true)
        whenever(logRepository.findById(job.logId)).thenReturn(Optional.of(log))

        whenever(emailStrategy.send(any(), any())).thenThrow(RuntimeException("SMTP Down"))

        worker.receive(json)

        verify(logRepository).update(check {
            assert(it.status == "ERROR") { "Status should be ERROR" }
            assert(it.errorMessage == "SMTP Down") { "Error message match" }
        })
    }
}