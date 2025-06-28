package com.vansh.socializebackend.features.notification.controller

import com.vansh.socializebackend.features.notification.model.Notification
import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.notification.service.NotificationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notification")
class NotificationController(
    private val notificationService: NotificationService
) {
    @GetMapping
    fun getUserNotifications(
        @RequestAttribute user: User
    ): MutableList<Notification>{
        return notificationService.getUserNotifications(user)
    }

    @PutMapping("/{notificationId}")
    fun markNotificationAsRead(@PathVariable notificationId: Long): Notification {
        return notificationService.markNotificationAsRead(notificationId)
    }
}