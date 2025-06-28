package com.vansh.socializebackend.features.auth.response

import com.vansh.socializebackend.features.feed.response.PostResponse

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val imageUrl: String,
    val posts: MutableList<PostResponse>
)
