package com.vansh.socializebackend.features.feed.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vansh.socializebackend.features.auth.model.User
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "posts")
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @NotEmpty
    val content: String,
    @Column(columnDefinition = "BYTEA")
    val postImage: ByteArray?,
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,
    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "posts_likes",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val likes: MutableSet<User> = mutableSetOf(),
    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<Comments> = mutableListOf(),
    @CreationTimestamp
    val creationDate: LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        if (content != other.content) return false
        if (postImage != null) {
            if (other.postImage == null) return false
            if (!postImage.contentEquals(other.postImage)) return false
        } else if (other.postImage != null) return false
        if (author != other.author) return false
        if (likes != other.likes) return false
        if (creationDate != other.creationDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + (postImage?.contentHashCode() ?: 0)
        result = 31 * result + author.hashCode()
        result = 31 * result + likes.hashCode()
        result = 31 * result + (creationDate?.hashCode() ?: 0)
        return result
    }
}