package pl.piomin.services.repository

import org.springframework.data.repository.CrudRepository
import pl.piomin.services.model.Person

interface PersonRepository : CrudRepository<Person, Long> {

    fun findByNationality(nationality: String): List<Person>
}