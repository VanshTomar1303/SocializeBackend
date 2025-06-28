package com.vansh.socializebackend.features.notification.service

import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.feed.model.Comments
import com.vansh.socializebackend.features.feed.model.Post
import com.vansh.socializebackend.features.messaging.model.Conversation
import com.vansh.socializebackend.features.messaging.model.Message
import com.vansh.socializebackend.features.notification.model.Notification
import com.vansh.socializebackend.model.*
import org.springframework.stereotype.Service

@Service
interface NotificationService {

    fun getUserNotifications(user: User): MutableList<Notification>
    fun markNotificationAsRead(notificationId: Long): Notification

    fun sendConversationToUser(senderId: Long,receiverId: Long, conversation: Conversation)
    fun sendMessageToConversation(conversationId: Long,message: Message)

    fun sendDeleteNotificationToPost(postId: Long)
    fun sendNewPostNotificationToFeed(post: Post)
    fun sendLikeToPost(postId: Long,likes: MutableSet<User>)
    fun sendCommentToPost(postId: Long,comments: Comments)
    fun sendDeleteCommentNotification(postId: Long,comments: Comments)
    fun sendCommentNotification(author: User, recipient: User, resourceId: Long)
    fun sendLikeNotification(author: User, recipient: User, resourceId: Long)
}