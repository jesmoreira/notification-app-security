package ntd.challenge.core.repository

import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import ntd.challenge.core.enums.Category
import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.model.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest
@Property(name = "micronaut.server.port", value = "-1")
class UserRepositoryTest {

    @Inject
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `should save and retrieve user`() {
        val user = User(
            name = "John Doe",
            email = "john@example.com",
            phoneNumber = "1234567890",
            subscribedCategories = setOf(Category.SPORTS),
            channels = setOf(ChannelType.EMAIL)
        )

        val savedUser = userRepository.save(user)

        assertNotNull(savedUser.id)
        assertEquals("John Doe", savedUser.name)
    }

    @Test
    fun `should find user by category with fetched channels`() {
        val user = User(
            name = "Sports Fan",
            email = "sports@example.com",
            phoneNumber = "111",
            subscribedCategories = setOf(Category.SPORTS),
            channels = setOf(ChannelType.SMS, ChannelType.EMAIL)
        )
        userRepository.save(user)

        val users = userRepository.findByCategoryWithChannels(Category.SPORTS)

        assertEquals(1, users.size)
        assertEquals(2, users[0].channels.size)
    }
}