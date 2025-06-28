package com.vansh.socializebackend.features.feed.repo

import com.vansh.socializebackend.features.feed.model.Comments
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepo: JpaRepository<Comments,Long> {
}