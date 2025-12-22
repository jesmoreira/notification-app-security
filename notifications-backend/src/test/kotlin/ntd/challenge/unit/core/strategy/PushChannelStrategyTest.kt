package ntd.challenge.unit.core.strategy

import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.strategy.PushChannelStrategy
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class PushChannelStrategyTest {

    private val pushChannelStrategy = PushChannelStrategy()

    @Test
    fun `supports should return true for PUSH_NOTIFICATION channel`() {
        assertTrue(pushChannelStrategy.supports(ChannelType.PUSH_NOTIFICATION))
    }

    @Test
    fun `supports should return false for EMAIL channel`() {
        assertFalse(pushChannelStrategy.supports(ChannelType.EMAIL))
    }

    @Test
    fun `supports should return false for SMS channel`() {
        assertFalse(pushChannelStrategy.supports(ChannelType.SMS))
    }

    @Test
    fun `send should execute without throwing exception`() {
        val destination = UUID.randomUUID().toString()
        val message = "Test push notification"

        // Should not throw
        pushChannelStrategy.send(destination, message)
    }

    @Test
    fun `send should handle UUID tokens`() {
        val destination = "550e8400-e29b-41d4-a716-446655440000"
        val message = "Push notification message"

        // Should not throw
        pushChannelStrategy.send(destination, message)
    }

    @Test
    fun `send should handle arbitrary token format`() {
        val destination = "fcm_token_abc123xyz"
        val message = "Test message"

        // Should not throw
        pushChannelStrategy.send(destination, message)
    }

    @Test
    fun `send should handle empty destination`() {
        val destination = ""
        val message = "Test message"

        // Should not throw
        pushChannelStrategy.send(destination, message)
    }

    @Test
    fun `send should handle empty message`() {
        val destination = UUID.randomUUID().toString()
        val message = ""

        // Should not throw
        pushChannelStrategy.send(destination, message)
    }

    @Test
    fun `send should handle long message content`() {
        val destination = UUID.randomUUID().toString()
        val message = "B".repeat(5000)

        // Should not throw
        pushChannelStrategy.send(destination, message)
    }
}
