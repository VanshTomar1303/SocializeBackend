package com.vansh.socializebackend.features.auth.util

import com.vansh.socializebackend.features.auth.service.AuthService
import com.vansh.socializebackend.features.auth.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val authService: AuthService
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader!=null && authHeader.startsWith("Bearer ")){
            if(jwtService.validateAccessToken(authHeader)){
                val userId = jwtService.getUserIdFromToken(authHeader)
                val role = jwtService.getUserRoleFromToken(authHeader)
                val user = authService.getUserById(userId)
                request.setAttribute("user", user)
                val authorities = listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
                val auth = UsernamePasswordAuthenticationToken(userId, null, authorities)
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request,response)
    }

}