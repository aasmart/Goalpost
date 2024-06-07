package io.aasmart.goalpost.compose.screens.auth

sealed class AuthUiEvent {
    data class SignupEmailChange(val value: String) : AuthUiEvent()
    data class SignupPassword1Change(val value: String) : AuthUiEvent()
    data class SignupPassword2Change(val value: String) : AuthUiEvent()
    data class SignupFirstNameChange(val value: String) : AuthUiEvent()

    object Signup : AuthUiEvent()

    data class LoginEmailChange(val value: String) : AuthUiEvent()
    data class LoginPasswordChange(val value: String) : AuthUiEvent()
    object Login : AuthUiEvent()

}