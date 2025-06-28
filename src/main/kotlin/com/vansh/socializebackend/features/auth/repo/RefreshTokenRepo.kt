package com.vansh.socializebackend.features.auth.repo

import com.vansh.socializebackend.features.auth.model.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepo: JpaRepository<RefreshToken,Long> {
    fun deleteByUserIdAndHashedToken(userId: Long, hashedToken: String)
    fun findByUserIdAndHashedToken(userId: Long, hashedToken: String)
}