package io.aasmart.goalpost.compose.state

data class AuthState(
    val loginEmail: String = "",
    val loginPassword: String = "",
    val signupEmail: String = "",
    val signupPassword1: String = "",
    val signupPassword2: String = "",
    val signupFirstName: String = "",
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
)
