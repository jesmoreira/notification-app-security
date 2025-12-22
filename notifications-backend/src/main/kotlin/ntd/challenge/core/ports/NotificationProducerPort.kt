package ntd.challenge.core.ports

import ntd.challenge.application.dto.NotificationJob

interface NotificationProducerPort {
    fun send(job: NotificationJob)
}