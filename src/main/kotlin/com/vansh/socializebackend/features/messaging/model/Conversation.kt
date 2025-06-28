package com.vansh.socializebackend.features.messaging.model

import com.vansh.socializebackend.features.auth.model.User
import jakarta.persistence.*

@Entity(name = "conversations")
data class Conversation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(optional = false)
    val author: User,
    @ManyToOne(optional = false)
    val recipient: User,
    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], orphanRemoval = true)
    val messages: MutableList<Message> = mutableListOf()
)