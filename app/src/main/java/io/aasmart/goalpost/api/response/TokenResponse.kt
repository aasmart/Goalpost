package io.aasmart.goalpost.api.response

data class TokenResponse(
    val access: String,
    val user: UserResponse
)
