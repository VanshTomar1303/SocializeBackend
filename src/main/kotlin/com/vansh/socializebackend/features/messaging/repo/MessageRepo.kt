package com.vansh.socializebackend.features.messaging.repo

import com.vansh.socializebackend.features.messaging.model.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepo: JpaRepository<Message,Long> {
}