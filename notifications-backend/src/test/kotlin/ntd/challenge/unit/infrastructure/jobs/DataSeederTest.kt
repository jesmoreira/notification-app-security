package ntd.challenge.unit.infrastructure.jobs

import io.micronaut.context.event.StartupEvent
import ntd.challenge.core.model.User
import ntd.challenge.core.repository.UserRepository
import ntd.challenge.infrastructure.jobs.DataSeeder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class DataSeederTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var startupEvent: StartupEvent

    @Test
    fun `should not seed when disabled`() {
        val dataSeeder = DataSeeder(userRepository, enabled = false, targetUserCount = 10)

        dataSeeder.onStartup(startupEvent)

        verify(userRepository, never()).count()
        verify(userRepository, never()).saveAll(any<Iterable<User>>())
    }

    @Test
    fun `should not seed when target count is already reached`() {
        val dataSeeder = DataSeeder(userRepository, enabled = true, targetUserCount = 10)

        whenever(userRepository.count()).thenReturn(15L)

        dataSeeder.onStartup(startupEvent)

        verify(userRepository).count()
        verify(userRepository, never()).saveAll(any<Iterable<User>>())
    }

    @Test
    fun `should seed static users when database is empty`() {
        val dataSeeder = DataSeeder(userRepository, enabled = true, targetUserCount = 5)

        whenever(userRepository.count()).thenReturn(0L)

        dataSeeder.onStartup(startupEvent)

        // 1 call for static users, 1 call for random users
        verify(userRepository, times(2)).saveAll(any<Iterable<User>>())
    }

    @Test
    fun `should seed only random users when database has some users but less than target`() {
        val dataSeeder = DataSeeder(userRepository, enabled = true, targetUserCount = 100)

        // Database has 5 users, target is 100. Should skip static users (count != 0)
        whenever(userRepository.count()).thenReturn(5L)

        dataSeeder.onStartup(startupEvent)

        // Should call saveAll for the remaining random users
        verify(userRepository, times(1)).saveAll(any<Iterable<User>>())
    }
}