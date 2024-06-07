package io.aasmart.goalpost.api

import android.content.SharedPreferences
import io.aasmart.goalpost.api.request.LoginRequest
import io.aasmart.goalpost.api.request.SignupRequest
import io.aasmart.goalpost.api.request.TokenRequest
import retrofit2.HttpException

class GoalpostRepositoryImpl(
    private val api: GoalpostApi,
    private val prefs: SharedPreferences
): GoalpostRepository {
    override suspend fun signUp(
        email: String,
        password1: String,
        password2: String,
        firstName: String
    ): AuthResult<Unit> {
        return try {
            api.signUp(
                request = SignupRequest(
                    email = email,
                    password1 = password1,
                    password2 = password2,
                    firstName = firstName
                )
            )
            api.login(
                LoginRequest(
                    email = email,
                    password = password1
                )
            )

            return AuthResult.Authorized()
        } catch (e: HttpException) {
            if(e.code() == 401)
                AuthResult.Unauthorized()
            else
                AuthResult.UnknownError()
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun login(email: String, password1: String): AuthResult<Unit> {
        return try {
            val response = api.login(
                LoginRequest(
                    email = email,
                    password = password1
                )
            )

            prefs.edit()
                .putString("token", response.access)
                .putString("first_name", response.user.firstName)
                .putLong("user_id", response.user.id)
                .apply()

            AuthResult.Authorized()
        } catch (e: HttpException) {
            if(e.code() == 401)
                AuthResult.Unauthorized()
            else
                AuthResult.UnknownError()
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun user(): AuthResult<Unit> {
        return try {
            val response = api.user()

            prefs.edit()
                .putString("first_name", response.firstName)
                .apply()

            AuthResult.Authorized()
        } catch (e: HttpException) {
            println(e.response()?.body() ?: "")
            if(e.code() == 401)
                AuthResult.Unauthorized()
            else
                AuthResult.UnknownError()
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun tokenVerify(): AuthResult<Unit> {
        return try {
            if(!prefs.contains("token"))
                return AuthResult.Unauthorized()

            api.tokenVerify(
                TokenRequest(
                    prefs.getString("token", "") ?: ""
                )
            )

            AuthResult.Authorized()
        } catch (e: HttpException) {
            prefs.edit()
                .remove("token")
                .apply()

            if(e.code() == 401)
                AuthResult.Unauthorized()
            else
                AuthResult.UnknownError()
        } catch (e: Exception) {
            prefs.edit()
                .remove("token")
                .apply()

            AuthResult.UnknownError()
        }
    }

}