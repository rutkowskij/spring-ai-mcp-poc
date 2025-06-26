package pl.piomin.services

import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import pl.piomin.services.tools.AccountTools

@SpringBootApplication
class AccountMCPService {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(AccountMCPService::class.java, *args)
        }
    }

    @Bean
    fun tools(accountTools: AccountTools): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
                .toolObjects(accountTools)
                .build()
    }

    // Commented code converted to Kotlin syntax
    /*
    @Bean
    fun prompts(): List<McpServerFeatures.SyncPromptSpecification> {
        val prompt = McpSchema.Prompt("persons-by-nationality", "Get persons by nationality",
                listOf(McpSchema.PromptArgument("nationality", "Person nationality", true)))

        val promptRegistration = McpServerFeatures.SyncPromptSpecification(prompt) { _, getPromptRequest ->
            val nameArgument = getPromptRequest.arguments()["name"] as String
            val userMessage = McpSchema.PromptMessage(McpSchema.Role.USER,
                    McpSchema.TextContent("How many persons are from $nameArgument ?"))
            McpSchema.GetPromptResult("Count persons by nationality", listOf(userMessage))
        }

        return listOf(promptRegistration)
    }
    */
}