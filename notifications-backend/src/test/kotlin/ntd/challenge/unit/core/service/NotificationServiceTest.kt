package ntd.challenge.core.service

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import ntd.challenge.application.dto.NotificationJob
import ntd.challenge.application.dto.SendNotificationRequest
import ntd.challenge.core.enums.Category
import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.model.NotificationLog
import ntd.challenge.core.model.User
import ntd.challenge.core.ports.NotificationProducerPort
import ntd.challenge.core.repository.NotificationLogRepository
import ntd.challenge.core.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var logRepository: NotificationLogRepository

    @Mock
    lateinit var producer: NotificationProducerPort

    @InjectMocks
    lateinit var notificationService: NotificationService

    @Test
    fun `submitNotification should create logs and send jobs`() {
        val request = SendNotificationRequest("SPORTS", "Message")
        val user = User(
            id = UUID.randomUUID(),
            name = "Test",
            email = "t@t.com",
            phoneNumber = "1",
            channels = setOf(ChannelType.SMS)
        )
        val log = NotificationLog(
            id = UUID.randomUUID(),
            user = user,
            category = Category.SPORTS,
            channel = ChannelType.SMS,
            messageBody = "Message",
            status = "PENDING"
        )

        whenever(userRepository.findByCategoryWithChannels(Category.SPORTS)).thenReturn(listOf(user))
        whenever(logRepository.save(any<NotificationLog>())).thenReturn(log)

        val result = notificationService.submitNotification(request)

        assertNotNull(result)
        verify(logRepository).save(any<NotificationLog>())
        verify(producer).send(any<NotificationJob>())
    }

    @Test
    fun `findLogs should return paged responses`() {
        val pageable = Pageable.from(0, 10)
        val user = User(name = "User", email = "e", phoneNumber = "p")
        val log = NotificationLog(
            id = UUID.randomUUID(),
            user = user,
            category = Category.SPORTS,
            channel = ChannelType.EMAIL,
            messageBody = "Msg",
            status = "SUCCESS"
        )
        val page = Page.of(listOf(log), pageable, 1)

        whenever(logRepository.searchLogs("", pageable)).thenReturn(page)

        val result = notificationService.findLogs("", pageable)

        assertEquals(1, result.content.size)
        assertEquals("User", result.content[0].userName)
    }
}