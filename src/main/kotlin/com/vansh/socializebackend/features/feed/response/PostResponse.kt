package com.vansh.socializebackend.features.feed.response

import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.feed.model.Comments

data class PostResponse(
    val id: Long,
    val content: String,
    val imageUrl: String,
    val author: User,
    val likes: MutableSet<User>,
    val comments: MutableList<Comments>
)

data class Response(
    val message: String
)