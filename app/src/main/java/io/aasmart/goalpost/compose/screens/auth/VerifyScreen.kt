package io.aasmart.goalpost.compose.screens.auth

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import io.aasmart.goalpost.api.AuthResult
import io.aasmart.goalpost.compose.GoalpostNav
import io.aasmart.goalpost.compose.components.LoadingWheel
import io.aasmart.goalpost.compose.viewmodels.AuthViewModel

@Composable
fun VerifyScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    goalpostNav: GoalpostNav
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when(result) {
                is AuthResult.Authorized ->
                    goalpostNav.home()
                is AuthResult.Unauthorized ->
                    goalpostNav.login()
                is AuthResult.UnknownError ->
                    goalpostNav.login()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.tokenVerify()
    }

    LoadingWheel {
        Text(text = "Verifying token...")
    }
}