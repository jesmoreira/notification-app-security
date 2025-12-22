package ntd.challenge.infrastructure.jobs

import io.micronaut.context.annotation.Value
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import net.datafaker.Faker
import ntd.challenge.core.enums.Category
import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.model.User
import ntd.challenge.core.repository.UserRepository
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.random.Random

@Singleton
open class DataSeeder(
    private val userRepository: UserRepository,
    @Value("\${seeder.enabled:false}") private val enabled: Boolean,
    @Value("\${seeder.users.count:100}") private val targetUserCount: Int
) {
    private val logger = LoggerFactory.getLogger(DataSeeder::class.java)
    private val faker = Faker()

    @EventListener
    @Transactional
    open fun onStartup(event: StartupEvent) {
        if (!enabled) {
            logger.info("Seeder is disabled.")
            return
        }

        val currentCount = userRepository.count()
        logger.info("Current user count in DB: $currentCount")

        if (currentCount >= targetUserCount) {
            logger.info("Target count ($targetUserCount) reached. Skipping seed.")
            return
        }

        if (currentCount == 0L) {
            logger.info("Database empty. Creating static users for testing...")
            createStaticUsers()
        }

        val usersToCreate = targetUserCount - userRepository.count()
        if (usersToCreate > 0) {
            logger.info("Generating $usersToCreate random users to reach target...")
            generateRandomUsers(usersToCreate.toInt())
        }

        logger.info("Seeding completed. Total users in DB: ${userRepository.count()}")
    }

    private fun createStaticUsers() {
        val staticUsers = listOf(
            User(
                name = "Joao John",
                email = "john@test.com",
                phoneNumber = "123456789",
                subscribedCategories = setOf(Category.SPORTS, Category.FINANCE),
                channels = setOf(ChannelType.SMS, ChannelType.EMAIL)
            ),
            User(
                name = "Maria Mary",
                email = "maria@test.com",
                phoneNumber = "987654321",
                subscribedCategories = setOf(Category.MOVIES),
                channels = setOf(ChannelType.PUSH_NOTIFICATION)
            ),
            User(
                name = "Admin User",
                email = "admin@test.com",
                phoneNumber = "000000000",
                subscribedCategories = Category.entries.toSet(),
                channels = ChannelType.entries.toSet()
            )
        )
        userRepository.saveAll(staticUsers)
    }

    private fun generateRandomUsers(count: Int) {
        val batchSize = 500
        val buffer = mutableListOf<User>()

        for (i in 1..count) {
            buffer.add(createRandomUser())

            if (buffer.size >= batchSize) {
                userRepository.saveAll(buffer)
                buffer.clear()
                logger.debug("Seeded batch of $batchSize users...")
            }
        }

        if (buffer.isNotEmpty()) {
            userRepository.saveAll(buffer)
        }
    }

    private fun createRandomUser(): User {
        val uniqueSuffix = UUID.randomUUID().toString().substring(0, 8)

        return User(
            name = faker.name().fullName(),
            email = "${faker.internet().slug()}.$uniqueSuffix@test.com",
            phoneNumber = faker.phoneNumber().cellPhone(),
            subscribedCategories = randomCategories(),
            channels = randomChannels()
        )
    }

    private fun randomCategories(): Set<Category> {
        val all = Category.entries
        val count = Random.nextInt(1, all.size + 1)
        return all.shuffled().take(count).toSet()
    }

    private fun randomChannels(): Set<ChannelType> {
        val all = ChannelType.entries
        val count = Random.nextInt(1, all.size + 1)
        return all.shuffled().take(count).toSet()
    }
}