package com.vansh.socializebackend.features.messaging.controller

import com.vansh.socializebackend.features.messaging.model.Conversation
import com.vansh.socializebackend.features.messaging.model.Message
import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.messaging.service.MessagingService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/messaging")
class MessagingController(
    private val messagingService: MessagingService
) {

    @GetMapping("/conversations")
    fun getConversation(@RequestAttribute("user") user: User): List<Conversation>{
        return messagingService.getConversationOfUser(user)
    }

    @GetMapping("/conversations/{conversationId}")
    fun getConversation(@RequestAttribute("user") user: User, @PathVariable conversationId: Long): Conversation {
        return messagingService.getConversation(user,conversationId)
    }

    @PostMapping("/conversations")
    fun createConversationAndAddMessage(
        @RequestAttribute("user") sender: User?,
        @RequestBody messageDto: MessageDto
    ): Conversation {
        return messagingService.createConversationAndAddMessage(sender!!, messageDto.receiverId, messageDto.content)
    }

    @PostMapping("/conversations/{conversationId}/messages")
    fun addMessageToConversation(
        @RequestAttribute("user") sender: User?,
        @RequestBody messageDto: MessageDto,
        @PathVariable conversationId: Long?
    ): Message {
        return messagingService.addMessageToConversation(
            conversationId!!, sender!!, messageDto.receiverId,
            messageDto.content
        )
    }

    @PutMapping("/conversations/messages/{messageId}")
    fun markMessageAsRead(
        @RequestAttribute("user") user: User?,
        @PathVariable messageId: Long?
    ){
        messagingService.markMessageAsRead(user!!, messageId!!)
    }
}

data class MessageDto(
    val receiverId: Long,
    val content: String
)