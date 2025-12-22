package ntd.challenge.unit.core.strategy

import ntd.challenge.core.enums.ChannelType
import ntd.challenge.core.strategy.SmsStrategy
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SmsStrategyTest {

    private val smsStrategy = SmsStrategy()

    @Test
    fun `supports should return true for SMS channel`() {
        assertTrue(smsStrategy.supports(ChannelType.SMS))
    }

    @Test
    fun `supports should return false for EMAIL channel`() {
        assertFalse(smsStrategy.supports(ChannelType.EMAIL))
    }

    @Test
    fun `supports should return false for PUSH_NOTIFICATION channel`() {
        assertFalse(smsStrategy.supports(ChannelType.PUSH_NOTIFICATION))
    }

    @Test
    fun `send should execute without throwing exception`() {
        val destination = "+1234567890"
        val message = "Test SMS message"

        // Should not throw
        smsStrategy.send(destination, message)
    }

    @Test
    fun `send should handle phone number with special characters`() {
        val destination = "+1 (234) 567-890"
        val message = "Test message"

        // Should not throw
        smsStrategy.send(destination, message)
    }

    @Test
    fun `send should handle empty destination`() {
        val destination = ""
        val message = "Test message"

        // Should not throw
        smsStrategy.send(destination, message)
    }

    @Test
    fun `send should handle empty message`() {
        val destination = "+1234567890"
        val message = ""

        // Should not throw
        smsStrategy.send(destination, message)
    }

    @Test
    fun `send should handle international phone numbers`() {
        val destination = "+441234567890"  // UK number
        val message = "International SMS test"

        // Should not throw
        smsStrategy.send(destination, message)
    }
}
