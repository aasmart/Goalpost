package io.aasmart.goalpost.api

import io.aasmart.goalpost.api.request.LoginRequest
import io.aasmart.goalpost.api.request.SignupRequest
import io.aasmart.goalpost.api.request.TokenRequest
import io.aasmart.goalpost.api.response.TokenResponse
import io.aasmart.goalpost.api.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GoalpostApi {
    @POST("auth/registration/")
    suspend fun signUp(
        @Body request: SignupRequest
    )

    @POST("auth/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): TokenResponse

    @POST("auth/token/verify/")
    suspend fun tokenVerify(
        @Body request: TokenRequest
    )

    @GET("auth/user/")
    suspend fun user(): UserResponse
}