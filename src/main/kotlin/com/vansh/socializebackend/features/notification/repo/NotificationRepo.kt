package com.vansh.socializebackend.features.notification.repo

import com.vansh.socializebackend.features.notification.model.Notification
import com.vansh.socializebackend.features.auth.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepo: JpaRepository<Notification,Long> {
    fun findByRecipient(recipient: User): MutableList<Notification>
    fun findByRecipientOrderByCreationDateDesc(user: User): MutableList<Notification>
}