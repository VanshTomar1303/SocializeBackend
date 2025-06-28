package com.vansh.socializebackend.features.messaging.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vansh.socializebackend.features.auth.model.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(optional = false)
    val author: User,
    @ManyToOne(optional = false)
    val recipient: User,
    @JsonIgnore
    @ManyToOne(optional = false)
    val conversation: Conversation,
    val content: String,
    val isRead: Boolean = false,
    @CreationTimestamp
    val createdAt: LocalDateTime? = null
)