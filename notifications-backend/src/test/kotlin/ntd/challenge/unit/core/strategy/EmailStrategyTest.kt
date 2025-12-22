package ntd.challenge.unit.core.strategy

import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.strategy.EmailStrategy
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmailStrategyTest {

    private val emailStrategy = EmailStrategy()

    @Test
    fun `supports should return true for EMAIL channel`() {
        assertTrue(emailStrategy.supports(ChannelType.EMAIL))
    }

    @Test
    fun `supports should return false for SMS channel`() {
        assertFalse(emailStrategy.supports(ChannelType.SMS))
    }

    @Test
    fun `supports should return false for PUSH_NOTIFICATION channel`() {
        assertFalse(emailStrategy.supports(ChannelType.PUSH_NOTIFICATION))
    }

    @Test
    fun `send should execute without throwing exception`() {
        val destination = "test@example.com"
        val message = "Test email message"

        // Should not throw
        emailStrategy.send(destination, message)
    }

    @Test
    fun `send should handle empty destination`() {
        val destination = ""
        val message = "Test message"

        // Should not throw
        emailStrategy.send(destination, message)
    }

    @Test
    fun `send should handle empty message`() {
        val destination = "test@example.com"
        val message = ""

        // Should not throw
        emailStrategy.send(destination, message)
    }

    @Test
    fun `send should handle long message`() {
        val destination = "test@example.com"
        val message = "A".repeat(10000)

        // Should not throw
        emailStrategy.send(destination, message)
    }
}
