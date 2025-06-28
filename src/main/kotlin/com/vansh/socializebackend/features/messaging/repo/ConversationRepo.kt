package com.vansh.socializebackend.features.messaging.repo

import com.vansh.socializebackend.features.messaging.model.Conversation
import com.vansh.socializebackend.features.auth.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ConversationRepo : JpaRepository<Conversation, Long> {
    fun findByAuthorAndRecipient(author: User, recipient: User): Optional<Conversation>
    fun findByAuthorOrRecipient(author: User, recipient: User): MutableList<Conversation>
}