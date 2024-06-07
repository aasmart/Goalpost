package io.aasmart.goalpost.compose.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import io.aasmart.goalpost.api.AuthResult
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.components.LoadingWheel
import io.aasmart.goalpost.compose.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    goalpostNav: GoalpostNav,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when(result) {
                is AuthResult.Authorized -> {
                    goalpostNav.home()
                }
                is AuthResult.Unauthorized -> Toast.makeText(
                    context,
                    "Unauthorized",
                    Toast.LENGTH_SHORT
                ).show()
                is AuthResult.UnknownError -> Toast.makeText(
                    context,
                    "Invalid username or password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    if(state.isLoading) {
        LoadingWheel()
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                value = state.loginEmail,
                label = { Text(text = "Email") },
                onValueChange = { viewModel.onEvent(AuthUiEvent.LoginEmailChange(it)) }
            )
            TextField(
                value = state.loginPassword,
                label = { Text(text = "Password") },
                onValueChange = { viewModel.onEvent(AuthUiEvent.LoginPasswordChange(it)) },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = { viewModel.onEvent(AuthUiEvent.Login) },
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                Text(text = "Login")
            }
            TextButton(onClick = goalpostNav.signup) {
                Text(text = "Don't have an account? Signup!")
            }
        }
    }
}