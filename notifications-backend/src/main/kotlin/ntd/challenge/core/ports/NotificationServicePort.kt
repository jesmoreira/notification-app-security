package ntd.challenge.core.ports

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import ntd.challenge.application.dto.NotificationLogResponse
import ntd.challenge.application.dto.SendNotificationRequest
import java.util.UUID

interface NotificationServicePort {
    fun submitNotification(request: SendNotificationRequest): UUID
    fun findLogs(search: String, pageable: Pageable): Page<NotificationLogResponse>
}