package io.aasmart.goalpost.api.response

import com.google.gson.annotations.SerializedName

@Suppress("")
data class UserResponse(
    val id: Long,
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val level: Long,
    val experience: Long,
)
