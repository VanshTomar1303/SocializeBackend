package com.vansh.socializebackend.features.auth.service

import com.vansh.socializebackend.features.auth.model.Role
import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.auth.response.UserResponse
import com.vansh.socializebackend.features.auth.service.serviceImplement.AuthServiceImpl.TokenPair
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
interface AuthService {
    fun register(username: String,email: String,password: String): User
    fun login(email: String,password: String,role: Role): TokenPair
    @Transactional
    fun refresh(refreshToken: String): TokenPair
    fun getUserById(id: Long): User
    fun uploadProfilePicture(file: MultipartFile?,user: User)
    fun getProfilePicture(user: User): ByteArray
    fun deleteUser(userId: Long)
    fun getUserResponse(userId: Long): UserResponse
    fun getProfilePictureById(userId: Long): ByteArray
}