package io.aasmart.goalpost.compose.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import io.aasmart.goalpost.api.AuthResult
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.components.LoadingWheel
import io.aasmart.goalpost.compose.viewmodels.AuthViewModel

@Composable
fun SignupScreen(
    goalpostNav: GoalpostNav,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when(result) {
                is AuthResult.Authorized -> {
                    goalpostNav.login()
                }
                is AuthResult.Unauthorized -> Toast.makeText(
                    context,
                    "Unauthorized",
                    Toast.LENGTH_SHORT
                ).show()
                is AuthResult.UnknownError -> Toast.makeText(
                    context,
                    "Invalid request",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    if(state.isLoading)
        LoadingWheel()
    else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = state.signupFirstName,
                label = { Text(text = "First Name") },
                onValueChange = { viewModel.onEvent(AuthUiEvent.SignupFirstNameChange(it)) }
            )
            TextField(
                value = state.signupEmail,
                label = { Text(text = "Email") },
                onValueChange = { viewModel.onEvent(AuthUiEvent.SignupEmailChange(it)) }
            )
            TextField(
                value = state.signupPassword1,
                label = { Text(text = "Password") },
                onValueChange = { viewModel.onEvent(AuthUiEvent.SignupPassword1Change(it)) },
                visualTransformation = PasswordVisualTransformation()
            )
            TextField(
                value = state.signupPassword2,
                label = { Text(text = "Password (again)") },
                onValueChange = { viewModel.onEvent(AuthUiEvent.SignupPassword2Change(it)) },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = { viewModel.onEvent(AuthUiEvent.Signup) }
            ) {
                Text(text = "Sign Up")
            }

            TextButton(onClick = goalpostNav.login) {
                Text(text = "Already have an account? Login!")
            }
        }
    }
}