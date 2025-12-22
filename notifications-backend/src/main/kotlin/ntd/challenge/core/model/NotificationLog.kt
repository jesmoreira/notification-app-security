package ntd.challenge.core.model

import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.*
import ntd.challenge.core.enums.Category
import ntd.challenge.core.enums.ChannelType
import java.time.LocalDateTime
import java.util.UUID

@Serdeable
@Entity
@Table(name = "notification_logs")
data class NotificationLog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: Category,

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    val channel: ChannelType,

    @Column(name = "message_body", nullable = false, columnDefinition = "TEXT")
    val messageBody: String,

    @Column(nullable = false)
    val status: String,

    @Column(name = "error_message", columnDefinition = "TEXT")
    val errorMessage: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)