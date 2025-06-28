package com.vansh.socializebackend.features.feed.service.serviceImplement

import com.vansh.socializebackend.features.feed.model.Comments
import com.vansh.socializebackend.features.feed.model.Post
import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.feed.repo.CommentRepo
import com.vansh.socializebackend.features.feed.repo.PostRepo
import com.vansh.socializebackend.features.auth.repo.UserRepo
import com.vansh.socializebackend.features.feed.service.FeedService
import com.vansh.socializebackend.features.notification.service.NotificationService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FeedServiceImpl(
    private val postRepo: PostRepo,
    private val commentRepo: CommentRepo,
    private val userRepo: UserRepo,
    private val notificationService: NotificationService
): FeedService {
    override fun createAPost(pic: MultipartFile, content: String, id: Long): Post {
        val author: User = userRepo.findById(id).orElseThrow { IllegalArgumentException("User not found.") }
        val post: Post = Post(
            author = author,
            postImage = pic.bytes,
            content = content
        )

        notificationService.sendNewPostNotificationToFeed(post)

        return postRepo.save(post)
    }

    override fun getPost(postId: Long): Post {
        return postRepo.findById(postId).orElseThrow{IllegalArgumentException("Post not found.")}
    }

    override fun deletePost(postId: Long, userId: Long) {
        val post: Post = postRepo.findById(postId).orElseThrow{IllegalArgumentException("Post not found.")}
        val user: User = userRepo.findById(userId).orElseThrow { IllegalArgumentException("User not found.") }

        if(post.author != user) throw IllegalArgumentException("User is not the author of the post.")

        notificationService.sendDeleteNotificationToPost(postId)
        postRepo.delete(post)
    }

    override fun likePost(postId: Long, userId: Long): Post {
        val post: Post = postRepo.findById(postId).orElseThrow{IllegalArgumentException("Post not found.")}
        val user: User = userRepo.findById(userId).orElseThrow { IllegalArgumentException("User not found.") }

        if (post.likes.contains(user)){
            post.likes.remove(user)
        }else{
            post.likes.add(user)
            notificationService.sendLikeNotification(user,post.author,post.id)
        }
        val savedPost: Post = postRepo.save(post)
        notificationService.sendLikeToPost(postId,savedPost.likes)

        return savedPost
    }

    override fun addComment(postId: Long, userId: Long, content: String): Comments {
        val post: Post = postRepo.findById(postId).orElseThrow{IllegalArgumentException("Post not found.")}
        val user: User = userRepo.findById(userId).orElseThrow { IllegalArgumentException("User not found.") }

        val comment: Comments = commentRepo.save(Comments(post = post, author = user, content = content))

//        val savedPost: Post = post.apply {
//                comments.add(comment)
//        }
//
//        postRepo.save(savedPost)

        notificationService.sendCommentNotification(user,post.author,post.id)
        notificationService.sendCommentToPost(postId,comment)

        return comment
    }

    override fun deleteComment(commentId: Long, userId: Long) {
        val comment: Comments = commentRepo.findById(commentId).orElseThrow { IllegalArgumentException("Comment not found") }
        val user: User = userRepo.findById(userId).orElseThrow { IllegalArgumentException("User not found.") }

        if (comment.author != user) throw IllegalArgumentException("User is not the author of the post.")

        commentRepo.delete(comment)
    }

    override fun getPostsByUserId(userId: Long): MutableList<Post> {
        return postRepo.findByAuthorId(userId)
    }

    override fun getFeedPosts(userId: Long): MutableList<Post> {
        val users: MutableList<User> = userRepo.findAll()
        val allUsersId: Set<Long> =  users.map {
            it.id
        }.toSet()

        return postRepo.findByAuthorIdInOrderByCreationDateDesc(allUsersId.toMutableSet())
    }

    override fun getAllPosts(): MutableList<Post> {
        return postRepo.findAllByOrderByCreationDateDesc()
    }

    override fun getPostComment(postId: Long): MutableList<Comments> {
        val post: Post = postRepo.findById(postId).orElseThrow{IllegalArgumentException("Post not found.")}
        return post.comments
    }

    override fun getPostLikes(postId: Long): MutableSet<User> {
        val post: Post = postRepo.findById(postId).orElseThrow{IllegalArgumentException("Post not found.")}
        return post.likes
    }

    override fun getImageBytes(postId: Long): ByteArray? {
        val post: Post = postRepo.findById(postId).orElseThrow { IllegalArgumentException("Post not found.") }

        return post.postImage
    }
}