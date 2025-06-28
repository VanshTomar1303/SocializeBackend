package com.vansh.socializebackend.features.auth.controller

import com.vansh.socializebackend.features.auth.model.Role
import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.auth.service.AuthService
import com.vansh.socializebackend.features.auth.service.serviceImplement.AuthServiceImpl
import com.vansh.socializebackend.features.feed.response.Response
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    data class LoginRequest(
        @field:Email(message = "Invalid email format.")
        val email: String,
        @field:Pattern(
            regexp =  "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
            message = "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
        )
        val password: String,
        val role: Role = Role.USER
    )

    data class RegisterRequest(
        @NotBlank(message = "Invalid username format.")
        val username: String,
        @field:Email(message = "Invalid email format.")
        val email: String,
        @field:Pattern(
            regexp =  "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
            message = "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
        )
        val password: String
    )

    data class RefreshToken(
        val refreshToken: String
    )

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody authRequest: RegisterRequest
    ){
        authService.register(authRequest.username,authRequest.email,authRequest.password)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody authRequest: LoginRequest
    ): AuthServiceImpl.TokenPair{
        return authService.login(authRequest.email,authRequest.password,authRequest.role)
    }

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody authRequest: RefreshToken
    ): AuthServiceImpl.TokenPair{
        return authService.refresh(authRequest.refreshToken)
    }

    @DeleteMapping("/delete")
    fun deleteUser(
        @RequestAttribute("user") user: User
    ): Response {
        authService.deleteUser(user.id)
        return Response("Account Deleted Successfully.")
    }

}