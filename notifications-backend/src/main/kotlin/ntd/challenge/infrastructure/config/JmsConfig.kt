package ntd.challenge.infrastructure.config

import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Value
import io.micronaut.jms.pool.JMSConnectionPool
import jakarta.inject.Named
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Factory
class JmsConfig(
    @Value("\${aws.region}") private val region: String,
    @Value("\${aws.services.sqs.endpoint}") private val endpoint: String,
    @Value("\${aws.access-key-id}") private val accessKey: String,
    @Value("\${aws.secret-key}") private val secretKey: String
) {

    @Singleton
    @Primary
    fun sqsClient(): SqsClient {
        return SqsClient.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
            )
            .build()
    }

    @Singleton
    @Named("connectionFactory")
    fun connectionFactory(sqsClient: SqsClient): JMSConnectionPool {
        val sqsFactory = SQSConnectionFactory(
            ProviderConfiguration(),
            sqsClient
        )

        return JMSConnectionPool(sqsFactory, 2, 10)
    }
}