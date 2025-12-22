package ntd.challenge.unit.infrastructure.config

import io.micronaut.jms.pool.JMSConnectionPool
import ntd.challenge.infrastructure.config.JmsConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import software.amazon.awssdk.services.sqs.SqsClient

@ExtendWith(MockitoExtension::class)
class JmsConfigTest {

    @Mock
    lateinit var sqsClient: SqsClient

    @Test
    fun `sqsClient should be configured correctly`() {
        val config = JmsConfig(
            region = "us-east-1",
            endpoint = "http://localhost:4566",
            accessKey = "test",
            secretKey = "test"
        )

        val client = config.sqsClient()

        assertNotNull(client)
    }

    @Test
    fun `connectionFactory should return JMSConnectionPool wrapping SqsClient`() {
        val config = JmsConfig(
            region = "us-east-1",
            endpoint = "http://localhost:4566",
            accessKey = "test",
            secretKey = "test"
        )

        val factory = config.connectionFactory(sqsClient)

        assertNotNull(factory)
        assertEquals(JMSConnectionPool::class.java, factory.javaClass)
    }
}