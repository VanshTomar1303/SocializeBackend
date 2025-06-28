package com.vansh.socializebackend.features.feed.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vansh.socializebackend.features.auth.model.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "comments")
data class Comments(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    val post: Post,
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,
    @Column(nullable = false)
    val content: String,
    @CreationTimestamp
    val creationTime: LocalDateTime? = null
)