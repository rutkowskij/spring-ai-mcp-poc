package pl.piomin.services.tools

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import pl.piomin.services.model.Person
import pl.piomin.services.repository.PersonRepository

@Service
class PersonTools(private val personRepository: PersonRepository) {

    @Tool(description = "Find person by ID")
    fun getPersonById(
            @ToolParam(description = "Person ID") id: Long): Person? {
        return personRepository.findById(id).orElse(null)
    }

    @Tool(description = "Find all persons by nationality")
    fun getPersonsByNationality(
            @ToolParam(description = "Nationality") nationality: String): List<Person> {
        return personRepository.findByNationality(nationality)
    }
}