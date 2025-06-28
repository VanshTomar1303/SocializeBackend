package com.vansh.socializebackend.features.auth.service.serviceImplement

import com.vansh.socializebackend.features.auth.model.Role
import com.vansh.socializebackend.features.auth.service.JwtService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.http.HttpStatus

@Service
class JwtServiceImpl(
    @Value("\${jwt.secret}") private val jwtSecret: String
): JwtService {

    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))

    val accessTokenValidityMs = 15L * 24 * 60 * 60 * 1000L // 15 days
    val refreshTokenValidityMs = 30L * 24 * 60 * 60 * 1000L // 30 days

    private fun generateToken(
        userId: Long,
        role: Role,
        type: String,
        expiry: Long
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", type)
            .claim("role", role.name) // 👈 add role here
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    override fun generateAccessToken(userId: Long, role: Role): String {
        return generateToken(userId, role, "access", accessTokenValidityMs)
    }

    override fun generateRefreshToken(userId: Long, role: Role): String {
        return generateToken(userId, role, "refresh", refreshTokenValidityMs)
    }

    override fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        return claims["type"] == "access"
    }

    override fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        return claims["type"] == "refresh"
    }

    override fun getUserIdFromToken(token: String): Long {
        val claims = parseAllClaims(token)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token")
        return claims.subject.toLong()
    }

    override fun getUserRoleFromToken(token: String): Role {
        val claims = parseAllClaims(token)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token")
        val roleString = claims["role"] as? String
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Role Missing in Token")
        return Role.valueOf(roleString)
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = if (token.startsWith("Bearer ")) token.removePrefix("Bearer ") else token
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception) {
            null
        }
    }
}
