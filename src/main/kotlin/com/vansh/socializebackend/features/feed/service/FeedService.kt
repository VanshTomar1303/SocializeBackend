package com.vansh.socializebackend.features.feed.service

import com.vansh.socializebackend.features.feed.model.Comments
import com.vansh.socializebackend.features.feed.model.Post
import com.vansh.socializebackend.features.auth.model.User
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
interface FeedService {
    fun createAPost(pic: MultipartFile,content: String,id: Long): Post
    fun getPost(postId: Long): Post
    fun deletePost(postId: Long, userId: Long)
    fun likePost(postId: Long,userId: Long): Post
    fun addComment(postId: Long,userId: Long,content: String): Comments
    fun deleteComment(commentId: Long,userId: Long)
    fun getPostsByUserId(userId: Long): MutableList<Post>
    fun getFeedPosts(userId: Long): MutableList<Post>
    fun getAllPosts(): MutableList<Post>
    fun getPostComment(postId: Long): MutableList<Comments>
    fun getPostLikes(postId: Long): MutableSet<User>
    fun getImageBytes(postId: Long): ByteArray?
}