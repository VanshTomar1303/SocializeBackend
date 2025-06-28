package com.vansh.socializebackend.features.auth.repo

import com.vansh.socializebackend.features.auth.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepo: JpaRepository<User,Long> {
    fun findUserByEmail(email: String): Optional<User>
}