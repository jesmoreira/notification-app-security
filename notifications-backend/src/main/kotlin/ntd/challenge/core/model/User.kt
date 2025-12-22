package ntd.challenge.core.model

import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.*
import ntd.challenge.core.enums.Category
import ntd.challenge.core.enums.ChannelType
import java.util.UUID

@Serdeable
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(name = "phone_number", nullable = false)
    val phoneNumber: String,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_subscriptions", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "subscribed_categories")
    @Enumerated(EnumType.STRING)
    val subscribedCategories: Set<Category> = emptySet(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_channels", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "channels")
    @Enumerated(EnumType.STRING)
    val channels: Set<ChannelType> = emptySet()
)