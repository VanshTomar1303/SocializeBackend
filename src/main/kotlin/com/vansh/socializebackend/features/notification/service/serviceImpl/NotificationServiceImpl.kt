package com.vansh.socializebackend.features.notification.service.serviceImpl

import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.feed.model.Comments
import com.vansh.socializebackend.features.feed.model.Post
import com.vansh.socializebackend.features.messaging.model.Conversation
import com.vansh.socializebackend.features.messaging.model.Message
import com.vansh.socializebackend.features.notification.model.Notification
import com.vansh.socializebackend.features.notification.model.NotificationType
import com.vansh.socializebackend.features.notification.repo.NotificationRepo
import com.vansh.socializebackend.features.notification.service.NotificationService
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val notificationRepo: NotificationRepo,
    private val messagingTemplate: SimpMessagingTemplate
): NotificationService {
    override fun getUserNotifications(user: User): MutableList<Notification> {
        return notificationRepo.findByRecipientOrderByCreationDateDesc(user)
    }

    override fun markNotificationAsRead(notificationId: Long): Notification {
        val notification: Notification = notificationRepo.findById(notificationId)
            .orElseThrow{IllegalArgumentException("Notification not found.")}

        val updatedNotification: Notification = notification.copy(
            isRead = true
        )

        messagingTemplate.convertAndSend("/topic/users/" + notification.recipient.id + "/notifications",
            updatedNotification)

        return notificationRepo.save(updatedNotification)
    }

    override fun sendConversationToUser(senderId: Long, receiverId: Long, conversation: Conversation) {
        messagingTemplate.convertAndSend("/topic/users/$senderId/conversations", conversation);
        messagingTemplate.convertAndSend("/topic/users/$receiverId/conversations", conversation);
    }

    override fun sendMessageToConversation(conversationId: Long, message: Message) {
        messagingTemplate.convertAndSend("/topic/conversations/$conversationId/messages", message);
    }

    override fun sendDeleteNotificationToPost(postId: Long) {
        messagingTemplate.convertAndSend("/topic/posts/$postId/delete", postId)
    }

    override fun sendNewPostNotificationToFeed(post: Post) {
        messagingTemplate.convertAndSend("/topic/feed/post",post)
    }

    override fun sendLikeToPost(postId: Long, likes: MutableSet<User>) {
        messagingTemplate.convertAndSend("/topic/likes/$postId",likes)
    }

    override fun sendCommentToPost(postId: Long, comments: Comments) {
        messagingTemplate.convertAndSend("/topics/comments/$postId",comments)
    }

    override fun sendDeleteCommentNotification(postId: Long, comments: Comments) {
        messagingTemplate.convertAndSend("/topic/comments/$postId/delete",comments)
    }

    override fun sendCommentNotification(author: User, recipient: User, resourceId: Long) {
        if (author.id == recipient.id) return
        val notification = Notification(
            actor = author,
            recipient = recipient,
            notificationType = NotificationType.COMMENT,
            resourceId = resourceId
        )

        notificationRepo.save(notification)

        messagingTemplate.convertAndSend("/topic/users/${recipient.id}/notification",notification)
    }

    override fun sendLikeNotification(author: User, recipient: User, resourceId: Long) {
        if (author.id == recipient.id) return
        val notification = Notification(
            actor = author,
            recipient = recipient,
            notificationType = NotificationType.LIKE,
            resourceId = resourceId
        )

        notificationRepo.save(notification)

        messagingTemplate.convertAndSend("/topic/users/${recipient.id}/notification",notification)
    }
}