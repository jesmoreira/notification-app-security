package ntd.challenge.core.strategy

import ntd.challenge.core.enums.ChannelType

interface NotificationChannelStrategy {
    fun supports(channel: ChannelType): Boolean
    fun send(destination: String, message: String)
}