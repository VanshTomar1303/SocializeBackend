package com.vansh.socializebackend.features.auth.model

import jakarta.persistence.*
import java.time.Instant

@Entity
data class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val expiresAt: Instant,
    val hashedToken: String,
    val createAt: Instant = Instant.now()
)