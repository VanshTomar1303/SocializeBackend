package com.vansh.socializebackend.features.auth.controller

import com.vansh.socializebackend.features.auth.model.User
import com.vansh.socializebackend.features.auth.response.UserResponse
import com.vansh.socializebackend.features.auth.service.AuthService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/user")
class UserController(
    private val authService: AuthService
){
    @GetMapping
    fun getCurrentUser(@RequestAttribute("user") user: User): UserResponse {
        return authService.getUserResponse(user.id)
    }

    @PatchMapping("/uploadImage", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun uploadImage(
        @RequestAttribute("user") user: User,
        @RequestParam("file", required = false) file: MultipartFile?
    ){
        authService.uploadProfilePicture(file,user)
    }
    @GetMapping("/profilePic", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getImage(
        @RequestAttribute("user") user: User
    ): ByteArray{
        return authService.getProfilePicture(user)
    }

    @GetMapping("/profilePic/{userId}")
    fun getProfilePicture(@PathVariable userId: Long): ByteArray{
        return authService.getProfilePictureById(userId)
    }
}