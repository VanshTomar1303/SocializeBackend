package com.vansh.socializebackend.features.auth.service

import com.vansh.socializebackend.features.auth.model.Role
import org.springframework.stereotype.Service

@Service
interface JwtService {
    fun generateAccessToken(userId: Long, role: Role): String
    fun generateRefreshToken(userId: Long, role: Role): String
    fun validateAccessToken(token: String): Boolean
    fun validateRefreshToken(token: String): Boolean
    fun getUserIdFromToken(token: String): Long
    fun getUserRoleFromToken(token: String): Role
}
