package ntd.challenge.core.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import ntd.challenge.core.enums.Category
import ntd.challenge.core.model.User
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<User, UUID> {

    @Query("""
        SELECT DISTINCT u FROM User u 
        LEFT JOIN FETCH u.channels 
        WHERE :category MEMBER OF u.subscribedCategories
    """)
    fun findByCategoryWithChannels(category: Category): List<User>
}