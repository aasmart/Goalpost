package io.aasmart.goalpost.api

interface GoalpostRepository {
    suspend fun signUp(
        email: String,
        password1: String,
        password2: String,
        firstName: String
    ): AuthResult<Unit>
    suspend fun login(email: String, password1: String): AuthResult<Unit>
    suspend fun user(): AuthResult<Unit>
    suspend fun tokenVerify(): AuthResult<Unit>

}