package pl.piomin.services.controller

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
@RequestMapping("/accounts")
class AccountController(
    chatClientBuilder: ChatClient.Builder,
    tools: ToolCallbackProvider
) {
    private val log: Logger = LoggerFactory.getLogger(PersonController::class.java)
    private val chatClient: ChatClient = chatClientBuilder
        .defaultToolCallbacks(tools)
        .build()

    @GetMapping("/count-by-person-id/{personId}")
    fun countByPersonId(@PathVariable personId: String): String {
        val pt = PromptTemplate("""
            How many accounts has person with {personId} ID ?
            """)
        val p = pt.create(mapOf("personId" to personId))
        return this.chatClient.prompt(p)
            .call()
            .content() ?: ""
    }

    @GetMapping("/balance-by-person-id/{personId}")
    fun balanceByPersonId(@PathVariable personId: String): String {
        val pt = PromptTemplate("""
            How many accounts has person with {personId} ID ?
            Return person name, nationality and a total balance on his/her accounts.
            """)
        val p = pt.create(mapOf("personId" to personId))
        return this.chatClient.prompt(p)
            .call()
            .content() ?: ""
    }
}
