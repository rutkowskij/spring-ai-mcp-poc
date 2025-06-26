package pl.piomin.services.controller

import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.spec.McpSchema
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/persons")
class PersonController(
    chatClientBuilder: ChatClient.Builder,
    tools: ToolCallbackProvider,
    private val mcpSyncClients: List<McpSyncClient>
) {
    private val log: Logger = LoggerFactory.getLogger(PersonController::class.java)
    private val chatClient: ChatClient = chatClientBuilder
        .defaultToolCallbacks(tools)
        .build()

    @GetMapping("/nationality/{nationality}")
    fun findByNationality(@PathVariable nationality: String): String {
        val pt = PromptTemplate("""
            Find persons with {nationality} nationality.
            """)
        val p = pt.create(mapOf("nationality" to nationality))
        return this.chatClient.prompt(p)
            .call()
            .content() ?: ""
    }

    @GetMapping("/count-by-nationality/{nationality}")
    fun countByNationality(@PathVariable nationality: String): String {
        val pt = PromptTemplate("""
            How many persons come from {nationality} ?
            """)
        val p = pt.create(mapOf("nationality" to nationality))
        return this.chatClient.prompt(p)
            .call()
            .content() ?: ""
    }

    @GetMapping("/count-by-nationality-from-client/{nationality}")
    fun countByNationalityFromClient(@PathVariable nationality: String): String {
        return this.chatClient.prompt(loadPromptByName("persons-by-nationality", nationality))
            .call()
            .content() ?: ""
    }

    fun loadPromptByName(name: String, nationality: String): Prompt {
        val r = McpSchema.GetPromptRequest(name, mapOf("nationality" to nationality))
        val client = mcpSyncClients.stream()
            .filter { c -> c.serverInfo.name() == "person-mcp-server" }
            .findFirst()

        return if (client.isPresent) {
            val content = client.get().getPrompt(r).messages().first().content() as McpSchema.TextContent
            val pt = PromptTemplate(content.text())
            val p = pt.create(mapOf("nationality" to nationality))
            log.info("Prompt: {}", p)
            p
        } else {
            throw IllegalStateException("No client found for person-mcp-server")
        }
    }
}
