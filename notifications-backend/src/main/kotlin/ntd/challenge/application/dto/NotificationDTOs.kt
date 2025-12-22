package ntd.challenge.application.dto

import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.UUID

@Serdeable
data class SendNotificationRequest(
    @field:NotBlank(message = "Category is required")
    val category: String,

    @field:NotBlank(message = "Message body is required")
    val message: String
)

@Serdeable
data class NotificationLogResponse(
    val id: UUID,
    val userName: String,
    val category: String,
    val channel: String,
    val message: String,
    val status: String,
    val timestamp: LocalDateTime
)

@Serdeable
data class NotificationJob(
    val logId: UUID,
    val userId: UUID,
    val userName: String,
    val destination: String,
    val type: String,
    val message: String
)