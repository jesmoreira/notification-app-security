package ntd.challenge.unit.application.controller

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpStatus
import ntd.challenge.application.controller.NotificationController
import ntd.challenge.application.dto.NotificationLogResponse
import ntd.challenge.application.dto.SendNotificationRequest
import ntd.challenge.core.ports.NotificationServicePort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class NotificationControllerTest {

    @Mock
    lateinit var notificationService: NotificationServicePort

    @InjectMocks
    lateinit var notificationController: NotificationController

    @Test
    fun `sendNotification should return ACCEPTED and trackingId`() {
        val request = SendNotificationRequest("FINANCE", "Message")
        val trackingId = UUID.randomUUID()

        whenever(notificationService.submitNotification(request)).thenReturn(trackingId)

        val response = notificationController.sendNotification(request)

        assertEquals(HttpStatus.ACCEPTED, response.status)
        assertEquals(
            trackingId, response.body()["trackingId"]
        )
    }

    @Test
    fun `getNotifications should return OK and Page`() {
        val pageable = Pageable.from(0, 10)
        val logResponse = NotificationLogResponse(
            UUID.randomUUID(),
            "User",
            "CAT",
            "SMS",
            "Msg",
            "SENT",
            LocalDateTime.now()
        )
        val page = Page.of(listOf(logResponse), pageable, 1L)

        whenever(notificationService.findLogs("search", pageable)).thenReturn(page)

        val response = notificationController.getNotifications("search", pageable)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(1, response.body().content.size)
    }
}