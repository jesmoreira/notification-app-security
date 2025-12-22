package ntd.challenge.core.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.PageableRepository
import ntd.challenge.core.model.NotificationLog
import java.util.UUID

@Repository
interface NotificationLogRepository : PageableRepository<NotificationLog, UUID> {

    @Query(
        value = """
            SELECT l FROM NotificationLog l
            JOIN FETCH l.user u
            WHERE (:search IS NULL OR :search = '' 
               OR LOWER(l.messageBody) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(l.category) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY l.createdAt DESC
        """,
        countQuery = """
            SELECT COUNT(l) FROM NotificationLog l
            JOIN l.user u
            WHERE (:search IS NULL OR :search = '' 
               OR LOWER(l.messageBody) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(l.category) LIKE LOWER(CONCAT('%', :search, '%')))
        """
    )
    fun searchLogs(search: String?, pageable: Pageable): Page<NotificationLog>
}