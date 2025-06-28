package com.vansh.socializebackend.features.auth.service.serviceImplement

import com.vansh.socializebackend.features.auth.util.HashedEncoder
import com.vansh.socializebackend.features.auth.model.RefreshToken
import com.vansh.socializebackend.features.auth.model.Role
import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.auth.repo.RefreshTokenRepo
import com.vansh.socializebackend.features.auth.repo.UserRepo
import com.vansh.socializebackend.features.auth.response.UserResponse
import com.vansh.socializebackend.features.auth.service.AuthService
import com.vansh.socializebackend.features.auth.service.JwtService
import com.vansh.socializebackend.features.feed.response.PostResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Service
class AuthServiceImpl(
    private val jwtService: JwtService,
    private val repo: UserRepo,
    private val hashedEncoder: HashedEncoder,
    private val refreshTokenRepo: RefreshTokenRepo,
): AuthService {

    @Value("\${refreshTokenValidityMs}") private var refreshTokenValidityMs: Long = 0

    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    override fun register(username: String, email: String, password: String): User {
        // Use .isPresent() to check if a user was found
        val existingUserOptional = repo.findUserByEmail(email.trim())

        if (existingUserOptional.isPresent) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User with email '${email.trim()}' already exists.")
        }

        // If not present, proceed with saving the new user
        return repo.save(
            User(
                email = email.trim(), // It's good practice to trim here too
                hashedPassword = hashedEncoder.encode(password),
                username = username
            )
        )
    }

    override fun login(email: String, password: String,role: Role): TokenPair {
        val userOptional = repo.findUserByEmail(email)
        if(userOptional.isEmpty){
            throw BadCredentialsException("Invalid Credentials.")
        }
        val user = userOptional.get()

        if(!hashedEncoder.matches(password,user.hashedPassword)){
            throw BadCredentialsException("Invalid Credentials.")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id,role)
        val newRefreshToken = jwtService.generateRefreshToken(user.id,role)

        storeRefreshToken(user.id,newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    override fun refresh(refreshToken: String): TokenPair {
        if(!jwtService.validateRefreshToken(refreshToken)){
            throw ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh Token.")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = repo.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh Token.")
        }

        val hashed = hashToken((refreshToken))
        refreshTokenRepo.findByUserIdAndHashedToken(user.id,hashed)

        refreshTokenRepo.deleteByUserIdAndHashedToken(user.id,hashed)

        val newAccessToken = jwtService.generateAccessToken(userId,user.role)
        val newRefreshToken = jwtService.generateRefreshToken(userId,user.role)

        storeRefreshToken(user.id,newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )

    }

    override fun getUserById(id: Long): User {
        val user: User = repo.findById(id).orElseThrow { IllegalArgumentException("Invalid Credentials.") }
        return user
    }

    override fun uploadProfilePicture(file: MultipartFile?,user: User) {
        val updatedUser = user.copy(
            imageData = file?.bytes
        )
        repo.save(updatedUser)
    }

    override fun getProfilePicture(user: User): ByteArray {
        return user.imageData!!
    }

    @Transactional
    override fun deleteUser(userId: Long) {
        repo.deleteById(userId)
    }

    private fun storeRefreshToken(userId: Long, rawRefreshToken: String){
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepo.save(
            RefreshToken(
                userId = userId,
                hashedToken = hashed,
                expiresAt = expiresAt,
            )
        )

    }

    @Transactional(readOnly = true)
    override fun getUserResponse(userId: Long): UserResponse {
        val user = repo.findById(userId).orElseThrow()

        // Force initialization of posts
        user.posts.size

        return user.toResponse()
    }

    override fun getProfilePictureById(userId: Long): ByteArray {
        val user: User = repo.findById(userId).orElseThrow {
            IllegalArgumentException("User not found.")
        }
        return user.imageData!!
    }

    private fun User.toResponse(): UserResponse {
        val postResponse = this.posts.map {
            PostResponse(
                id = it.id,
                author = it.author,
                content = it.content,
                imageUrl = "/feed/posts/${it.id}/image",
                likes = it.likes,
                comments = it.comments,
            )
        }.toMutableList()

        return UserResponse(
            id = this.id,
            username = this.username,
            email = this.email,
            imageUrl = "/user/profilePic",
            posts = postResponse
        )
    }

    private fun hashToken(token: String): String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())

        return Base64.getEncoder().encodeToString(hashBytes)
    }
}