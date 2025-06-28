package com.vansh.socializebackend.features.messaging.service.serviceImplement

import com.vansh.socializebackend.features.messaging.model.Conversation
import com.vansh.socializebackend.features.messaging.model.Message
import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.messaging.repo.ConversationRepo
import com.vansh.socializebackend.features.messaging.repo.MessageRepo
import com.vansh.socializebackend.features.auth.service.AuthService
import com.vansh.socializebackend.features.messaging.service.MessagingService
import com.vansh.socializebackend.features.notification.service.NotificationService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class MessagingServiceImpl(
    private val conversationRepo: ConversationRepo,
    private val notificationService: NotificationService,
    private val messageRepo: MessageRepo,
    private val authService: AuthService,

    ): MessagingService {
    override fun getConversationOfUser(user: User): List<Conversation> {
        return conversationRepo.findByAuthorOrRecipient(user,user)
    }

    override fun getConversation(user: User, conversationId: Long): Conversation {
        val conversation: Conversation = conversationRepo.findById(conversationId)
            .orElseThrow{ IllegalArgumentException("Conversation not found")}
        if (conversation.author.id != user.id
            && conversation.recipient.id != user.id
        ) {
            throw IllegalArgumentException("User not authorized to view conversation")
        }
        return conversation;
    }

    @Transactional
    override fun createConversationAndAddMessage(sender: User, receiverId: Long, content: String): Conversation {
        val receiver = authService.getUserById(receiverId)

        if (conversationRepo.findByAuthorAndRecipient(sender, receiver).isPresent ||
            conversationRepo.findByAuthorAndRecipient(receiver, sender).isPresent) {
            throw IllegalArgumentException("Conversation already exists, use the conversation id to send messages.")
        }

        val conversation = conversationRepo.save(Conversation(author = sender, recipient = receiver))
        val message = Message(author = sender, recipient = receiver, conversation = conversation, content = content)

        messageRepo.save(message)
        conversation.messages.add(message)
        notificationService.sendConversationToUser(sender.id, receiver.id, conversation)
        return conversation
    }


    override fun addMessageToConversation(
        conversationId: Long,
        sender: User,
        receiverId: Long,
        content: String
    ): Message {
        val receiver = authService.getUserById(receiverId)
        val conversation: Conversation = conversationRepo.findById(conversationId)
            .orElseThrow { IllegalArgumentException("Conversation not found") }
        if (conversation.author.id != sender.id
            && conversation.recipient.id != sender.id) {
            throw IllegalArgumentException("User not authorized to send message to this conversation");
        }

        if (conversation.author.id != receiver.id
            && conversation.recipient.id != receiver.id) {
            throw IllegalArgumentException("Receiver is not part of this conversation");
        }

        val message = Message(
            author = sender,
            recipient = receiver,
            conversation = conversation,
            content = content
        )
        messageRepo.save(message);
        conversation.messages.add(message);
        notificationService.sendMessageToConversation(conversation.id,message)
        notificationService.sendConversationToUser(sender.id,receiver.id,conversation)
        return message
    }

    override fun markMessageAsRead(user: User, messageId: Long) {
        val message: Message = messageRepo.findById(messageId)
            .orElseThrow{ IllegalArgumentException("Message not found.") }

        if (message.recipient.id != user.id) {
            throw IllegalArgumentException("User not authorized to mark message as read");
        }
        if (!message.isRead) {
            val updateMessage: Message = message.copy(
                isRead = true
            )
            messageRepo.save(updateMessage);
            notificationService.sendMessageToConversation(updateMessage.conversation.id, updateMessage);
        }
    }
}