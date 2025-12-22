package ntd.challenge.core.repository

import io.micronaut.context.annotation.Property
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import ntd.challenge.core.enums.Category
import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.model.NotificationLog
import ntd.challenge.core.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest
@Property(name = "micronaut.server.port", value = "-1")
class NotificationLogRepositoryTest {

    @Inject
    private lateinit var notificationLogRepository: NotificationLogRepository

    @Inject
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        notificationLogRepository.deleteAll()
        userRepository.deleteAll()

        testUser = userRepository.save(
            User(
                name = "Test User",
                email = "test@example.com",
                phoneNumber = "1234567890",
                subscribedCategories = setOf(Category.SPORTS),
                channels = setOf(ChannelType.EMAIL)
            )
        )
    }

    @Test
    fun `should save notification log successfully`() {
        val log = NotificationLog(
            user = testUser,
            category = Category.SPORTS,
            channel = ChannelType.EMAIL,
            messageBody = "Test message",
            status = "PENDING"
        )

        val savedLog = notificationLogRepository.save(log)

        assertNotNull(savedLog.id)
        assertEquals("PENDING", savedLog.status)
        assertEquals("Test message", savedLog.messageBody)
    }

    @Test
    fun `should save log with SUCCESS status`() {
        val log = NotificationLog(
            user = testUser,
            category = Category.FINANCE,
            channel = ChannelType.SMS,
            messageBody = "Success test",
            status = "SUCCESS"
        )

        val savedLog = notificationLogRepository.save(log)

        assertEquals("SUCCESS", savedLog.status)
    }

    @Test
    fun `should search logs by message content`() {
        val log1 = NotificationLog(
            user = testUser,
            category = Category.SPORTS,
            channel = ChannelType.EMAIL,
            messageBody = "Find this message",
            status = "PENDING"
        )
        val log2 = NotificationLog(
            user = testUser,
            category = Category.FINANCE,
            channel = ChannelType.SMS,
            messageBody = "Different message",
            status = "SUCCESS"
        )

        notificationLogRepository.save(log1)
        notificationLogRepository.save(log2)

        val pageable = Pageable.from(0, 10)
        val results = notificationLogRepository.searchLogs("Find this", pageable)

        assertEquals(1, results.content.size)
        assertEquals("Find this message", results.content.first().messageBody)
    }

    @Test
    fun `should search logs by user name`() {
        val log = NotificationLog(
            user = testUser,
            category = Category.SPORTS,
            channel = ChannelType.EMAIL,
            messageBody = "User search test",
            status = "PENDING"
        )

        notificationLogRepository.save(log)

        val pageable = Pageable.from(0, 10)
        val results = notificationLogRepository.searchLogs("Test User", pageable)

        assertEquals(1, results.content.size)
        assertEquals("Test User", results.content.first().user.name)
    }

    @Test
    fun `should search logs by category`() {
        val log1 = NotificationLog(
            user = testUser,
            category = Category.SPORTS,
            channel = ChannelType.EMAIL,
            messageBody = "Sports message",
            status = "PENDING"
        )
        val log2 = NotificationLog(
            user = testUser,
            category = Category.FINANCE,
            channel = ChannelType.SMS,
            messageBody = "Finance message",
            status = "SUCCESS"
        )

        notificationLogRepository.save(log1)
        notificationLogRepository.save(log2)

        val pageable = Pageable.from(0, 10)
        val results = notificationLogRepository.searchLogs("SPORTS", pageable)

        assertEquals(1, results.content.size)
        assertEquals(Category.SPORTS, results.content.first().category)
    }

    @Test
    fun `should return results sorted by created_at descending`() {
        val log1 = NotificationLog(
            user = testUser,
            category = Category.SPORTS,
            channel = ChannelType.EMAIL,
            messageBody = "First message",
            status = "PENDING"
        )
        notificationLogRepository.save(log1)

        Thread.sleep(100)

        val log2 = NotificationLog(
            user = testUser,
            category = Category.SPORTS,
            channel = ChannelType.SMS,
            messageBody = "Second message",
            status = "SUCCESS"
        )
        notificationLogRepository.save(log2)

        val pageable = Pageable.from(0, 10)
        val results = notificationLogRepository.searchLogs("", pageable)

        assertTrue(results.content.size >= 2)
        assertEquals("Second message", results.content[0].messageBody)
        assertEquals("First message", results.content[1].messageBody)
    }

    @Test
    fun `should paginate results correctly`() {
        repeat(15) { i ->
            notificationLogRepository.save(
                NotificationLog(
                    user = testUser,
                    category = Category.SPORTS,
                    channel = ChannelType.EMAIL,
                    messageBody = "Message $i",
                    status = "PENDING"
                )
            )
        }

        val pageableFirst = Pageable.from(0, 10)
        val firstPage = notificationLogRepository.searchLogs("", pageableFirst)

        assertEquals(10, firstPage.content.size)
        // Note: For Micronaut Data Page, totalPages returns the total page count, not items.
        // If 15 items and page size 10 -> 2 pages.
        // If you want total items, use totalElements (Long).
        // Assuming user intention was to check total items:
        assertEquals(15L, firstPage.totalSize)

        val pageableSecond = Pageable.from(1, 10)
        val secondPage = notificationLogRepository.searchLogs("", pageableSecond)

        assertEquals(5, secondPage.content.size)
    }

    @Test
    fun `should update log status`() {
        val log = NotificationLog(
            user = testUser,
            category = Category.SPORTS,
            channel = ChannelType.EMAIL,
            messageBody = "Test",
            status = "PENDING"
        )

        val savedLog = notificationLogRepository.save(log)
        val logId = savedLog.id!!

        val updatedLog = savedLog.copy(status = "SUCCESS")
        notificationLogRepository.update(updatedLog)

        val retrievedLog = notificationLogRepository.findById(logId).orElseThrow()
        assertEquals("SUCCESS", retrievedLog.status)
    }
}