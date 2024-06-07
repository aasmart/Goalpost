package io.aasmart.goalpost.api.request

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    val email: String,
    val password1: String,
    val password2: String,
    @SerializedName("first_name")
    val firstName: String
)
