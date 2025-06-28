package com.vansh.socializebackend.features.messaging.service

import com.vansh.socializebackend.features.messaging.model.Conversation
import com.vansh.socializebackend.features.messaging.model.Message
import com.vansh.socializebackend.features.auth.model.User
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
interface MessagingService {
    fun getConversationOfUser(user: User): List<Conversation>
    fun getConversation(user: User, conversationId: Long): Conversation
    @Transactional
    fun createConversationAndAddMessage(sender: User, receiverId: Long, content: String): Conversation
    fun addMessageToConversation(conversationId: Long, sender: User, receiverId: Long, content: String): Message
    fun markMessageAsRead(user: User, messageId: Long)
}