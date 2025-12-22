package ntd.challenge.application.controller

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.validation.Validated
import jakarta.validation.Valid
import ntd.challenge.application.dto.NotificationLogResponse
import ntd.challenge.application.dto.SendNotificationRequest
import ntd.challenge.core.ports.NotificationServicePort
import java.util.UUID

@Validated
@Controller("/api/v1/notifications")
class NotificationController(
    private val notificationService: NotificationServicePort,
) {

    @Post
    fun sendNotification(@Body @Valid request: SendNotificationRequest): HttpResponse<Map<String, UUID>> {
        val trackingId = notificationService.submitNotification(request)

        return HttpResponse
            .status<Map<String, UUID>>(HttpStatus.ACCEPTED)
            .body(mapOf("trackingId" to trackingId))
    }

    @Get
    @ExecuteOn(TaskExecutors.BLOCKING)
    fun getNotifications(
        @QueryValue(defaultValue = "") search: String,
        pageable: Pageable
    ): HttpResponse<Page<NotificationLogResponse>> {
        val logs = notificationService.findLogs(search, pageable)
        return HttpResponse.ok(logs)
    }
}