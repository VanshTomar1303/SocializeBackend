package com.vansh.socializebackend.features.feed.repo

import com.vansh.socializebackend.features.feed.model.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepo: JpaRepository<Post,Long> {
    fun findByAuthorId(authorId: Long): MutableList<Post>
    fun findAllByOrderByCreationDateDesc(): MutableList<Post>
    fun findByAuthorIdInOrderByCreationDateDesc(authorId: MutableSet<Long>): MutableList<Post>
}