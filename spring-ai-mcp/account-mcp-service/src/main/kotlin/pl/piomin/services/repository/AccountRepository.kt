package pl.piomin.services.repository

import org.springframework.data.repository.CrudRepository
import pl.piomin.services.model.Account

interface AccountRepository : CrudRepository<Account, Long> {
    fun findByPersonId(personId: Long): List<Account>
}