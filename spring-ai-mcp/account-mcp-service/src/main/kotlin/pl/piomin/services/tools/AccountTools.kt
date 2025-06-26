package pl.piomin.services.tools

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import pl.piomin.services.model.Account
import pl.piomin.services.repository.AccountRepository

@Service
class AccountTools(private val accountRepository: AccountRepository) {

    @Tool(description = "Find all accounts by person ID")
    fun getAccountsByPersonId(
        @ToolParam(description = "Person ID") personId: Long
    ): List<Account> {
        return accountRepository.findByPersonId(personId)
    }
}