package com.vansh.socializebackend.features.auth.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vansh.socializebackend.features.messaging.model.Conversation
import com.vansh.socializebackend.features.notification.model.Notification
import com.vansh.socializebackend.features.feed.model.Post
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val username: String,
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(nullable = false)
    val hashedPassword: String,
    @Column(columnDefinition = "BYTEA")
    val imageData: ByteArray? = null,
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,
    @JsonIgnore
    @OneToMany(mappedBy = "recipient", cascade = [CascadeType.ALL], orphanRemoval = true)
    val receivedNotifications: MutableList<Notification> = mutableListOf(),
    @JsonIgnore
    @OneToMany(mappedBy = "actor", cascade = [CascadeType.ALL], orphanRemoval = true)
    val actedNotifications: MutableList<Notification> = mutableListOf(),
    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    val conversationsAsAuthor: MutableList<Conversation> = mutableListOf(),
    @JsonIgnore
    @OneToMany(mappedBy = "recipient", cascade = [CascadeType.ALL], orphanRemoval = true)
    val receivedConversation: MutableList<Conversation> = mutableListOf(),
    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
   val posts: MutableList<Post> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (hashedPassword != other.hashedPassword) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (role != other.role) return false
        if (receivedNotifications != other.receivedNotifications) return false
        if (actedNotifications != other.actedNotifications) return false
        if (conversationsAsAuthor != other.conversationsAsAuthor) return false
        if (receivedConversation != other.receivedConversation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + hashedPassword.hashCode()
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + receivedNotifications.hashCode()
        result = 31 * result + actedNotifications.hashCode()
        result = 31 * result + conversationsAsAuthor.hashCode()
        result = 31 * result + receivedConversation.hashCode()
        return result
    }
}

