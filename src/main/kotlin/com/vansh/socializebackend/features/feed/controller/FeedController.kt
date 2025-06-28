package com.vansh.socializebackend.features.feed.controller

import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.feed.model.Comments
import com.vansh.socializebackend.features.feed.model.Post
import com.vansh.socializebackend.features.feed.response.PostResponse
import com.vansh.socializebackend.features.feed.response.Response
import com.vansh.socializebackend.features.feed.service.FeedService
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/feed")
class FeedController(
    private val feedService: FeedService
) {
    @GetMapping
    fun getFeedPosts(@RequestAttribute("user") user: User): ResponseEntity<MutableList<PostResponse>>{
        val posts: MutableList<Post> = feedService.getFeedPosts(user.id)
        val response = postToResponse(posts)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/post")
    fun getAllPosts(): ResponseEntity<MutableList<PostResponse>>{
        val posts: MutableList<Post> = feedService.getAllPosts()
        val response = postToResponse(posts)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/post")
    fun createPost(
        @RequestParam(value = "picture", required = false) file: MultipartFile,
        @RequestParam(value = "content") content: String,
        @RequestAttribute("user") user: User
    ): ResponseEntity<Post>{
        val post = feedService.createAPost(file,content,user.id)
        return ResponseEntity.ok(post)
    }

    @GetMapping("/post/{postId}", produces = ["multipart/mixed"])
    fun getPost(@PathVariable postId: Long): ResponseEntity<MultiValueMap<String, Any>> {
        val post = feedService.getPost(postId)

        val imageBytes = post.postImage // assuming ByteArray or load from DB/path

        val metadataPart = HttpEntity(post) // JSON part
        val imageHeaders = HttpHeaders().apply {
            contentType = MediaType.IMAGE_PNG
            contentDisposition = ContentDisposition.inline().filename("post-image.png").build()
        }
        val imagePart = HttpEntity(imageBytes, imageHeaders)

        val parts = LinkedMultiValueMap<String, Any>().apply {
            add("metadata", metadataPart)
            add("image", imagePart)
        }

        return ResponseEntity.ok(parts)
    }


    @DeleteMapping("/post/{postId}")
    fun deletePost(
        @PathVariable postId: Long,
        @RequestAttribute("user") user: User
    ): ResponseEntity<Response>{
        feedService.deletePost(postId,user.id)
        return ResponseEntity.ok(Response("Post deleted successfully."))
    }

    @PostMapping("/post/{postId}/comments")
    fun addComment(
        @PathVariable postId: Long,
        @RequestParam(value = "content") content: String,
        @RequestAttribute("user") user: User
    ): ResponseEntity<Comments>{
        val comment: Comments = feedService.addComment(postId,user.id,content)

        return ResponseEntity.ok(comment)
    }

    @GetMapping("/post/{postId}/comments")
    fun getComments(@PathVariable postId: Long): ResponseEntity<MutableList<Comments>>{
        val comments: MutableList<Comments> = feedService.getPostComment(postId)
        return ResponseEntity.ok(comments)
    }

    @DeleteMapping("/comments/{commentId}")
    fun deleteComments(
        @PathVariable commentId: Long,
        @RequestAttribute("user") user: User
    ): ResponseEntity<Response>{
        feedService.deleteComment(commentId,user.id)
        return ResponseEntity.ok(Response("Comment Deleted Successfully."))
    }

    @PutMapping("/posts/{postId}/like")
    fun likePost(
        @PathVariable postId: Long,
        @RequestAttribute("user") user: User
    ): ResponseEntity<Post>{
        val post: Post = feedService.likePost(postId,user.id)
        return ResponseEntity.ok(post)
    }

    @GetMapping("/posts/{postId}/likes")
    fun getPostLikes(@PathVariable postId: Long): ResponseEntity<MutableSet<User>>{
        val likes: MutableSet<User> = feedService.getPostLikes(postId)
        return ResponseEntity.ok(likes)
    }

    @GetMapping("/posts/user/{userId}")
    fun getPostsByUserId(
        @PathVariable userId: Long
    ):ResponseEntity<MutableList<PostResponse>>{
        val posts: MutableList<Post> = feedService.getPostsByUserId(userId)
        val response = postToResponse(posts)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/posts/{postId}/image", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getPostImage(@PathVariable postId: Long): ResponseEntity<ByteArray> {
        val imageBytes = feedService.getImageBytes(postId)
        return ResponseEntity.ok(imageBytes)
    }

    private fun postToResponse(posts: MutableList<Post>): MutableList<PostResponse>{
        val response: MutableList<PostResponse> = posts.map {
            PostResponse(
                id = it.id,
                comments = it.comments,
                likes = it.likes,
                content = it.content,
                imageUrl = "/feed/posts/${it.id}/image",
                author = it.author,
            )
        }.toMutableList()

        return response
    }
}
