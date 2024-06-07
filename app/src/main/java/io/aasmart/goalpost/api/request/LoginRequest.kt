package io.aasmart.goalpost.api.request

data class LoginRequest(
    val email: String,
    val password: String,
)
