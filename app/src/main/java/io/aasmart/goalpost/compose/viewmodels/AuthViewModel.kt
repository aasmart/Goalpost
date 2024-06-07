package io.aasmart.goalpost.compose.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.aasmart.goalpost.api.AuthResult
import io.aasmart.goalpost.api.GoalpostRepository
import io.aasmart.goalpost.compose.state.AuthState
import io.aasmart.goalpost.compose.screens.auth.AuthUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: GoalpostRepository
) : ViewModel() {

    var state by mutableStateOf(AuthState())

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    fun onEvent(event: AuthUiEvent) {
        when(event) {
            is AuthUiEvent.SignupEmailChange ->
                state = state.copy(signupEmail = event.value)
            is AuthUiEvent.SignupPassword1Change ->
                state = state.copy(signupPassword1 = event.value)
            is AuthUiEvent.SignupPassword2Change ->
                state = state.copy(signupPassword2 = event.value)
            is AuthUiEvent.SignupFirstNameChange ->
                state = state.copy(signupFirstName = event.value)
            AuthUiEvent.Signup -> signup()

            is AuthUiEvent.LoginEmailChange ->
                state = state.copy(loginEmail = event.value)
            is AuthUiEvent.LoginPasswordChange ->
                state = state.copy(loginPassword = event.value)
            AuthUiEvent.Login -> login()
        }
    }

    private fun signup() = viewModelScope.launch {
        state = state.copy(isLoading = true)
        val result = repository.signUp(
            email = state.signupEmail,
            password1 = state.signupPassword1,
            password2 = state.signupPassword2,
            firstName = state.signupFirstName
        )
        state = state.copy(isLoading = false)
        resultChannel.send(result)
    }

    private fun login() = viewModelScope.launch {
        state = state.copy(isLoading = true)
        val result = repository.login(
            email = state.loginEmail,
            password1 = state.loginPassword
        )
        resultChannel.send(result)
        state = state.copy(isLoading = false)
    }

    fun tokenVerify() = viewModelScope.launch {
        state = state.copy(isLoading = true)
        val result = repository.tokenVerify()
        resultChannel.send(result)
        state = state.copy(isLoading = false)
    }
}