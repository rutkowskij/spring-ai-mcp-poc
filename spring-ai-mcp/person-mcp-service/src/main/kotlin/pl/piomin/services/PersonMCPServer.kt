package pl.piomin.services

import io.modelcontextprotocol.server.McpServerFeatures
import io.modelcontextprotocol.spec.McpSchema
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import pl.piomin.services.tools.PersonTools

@SpringBootApplication
class PersonMCPServer {

    @Bean
    fun tools(personTools: PersonTools): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
                .toolObjects(personTools)
                .build()
    }

    @Bean
    fun prompts(): List<McpServerFeatures.SyncPromptSpecification> {
        val prompt = McpSchema.Prompt("persons-by-nationality", "Get persons by nationality",
                listOf(McpSchema.PromptArgument("nationality", "Person nationality", true)))

        val promptRegistration = McpServerFeatures.SyncPromptSpecification(prompt) { exchange, getPromptRequest ->
            val argument = getPromptRequest.arguments()["nationality"] as String
            val userMessage = McpSchema.PromptMessage(McpSchema.Role.USER,
                    McpSchema.TextContent("How many persons come from $argument ?"))
            McpSchema.GetPromptResult("Count persons by nationality", listOf(userMessage))
        }

        return listOf(promptRegistration)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(PersonMCPServer::class.java, *args)
        }
    }
}