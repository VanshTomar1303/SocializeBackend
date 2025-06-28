package com.vansh.socializebackend.features.notification.model

import com.vansh.socializebackend.features.auth.model.User
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity(name = "Notification")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    val recipient: User,
    @ManyToOne
    val actor: User,
    val isRead: Boolean = false,
    val notificationType: NotificationType,
    val resourceId: Long,
    @CreationTimestamp val creationDate: LocalDateTime? = null
)

enum class NotificationType{
    LIKE,COMMENT
}
