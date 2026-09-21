package com.kasimarketplace.app.data.repository

import com.kasimarketplace.app.data.local.SessionManager
import com.kasimarketplace.app.data.model.LoginRequest
import com.kasimarketplace.app.data.model.RegisterRequest
import com.kasimarketplace.app.data.remote.ApiService

/**
 * Handles registration and login. The password is sent once, over HTTPS, to the API,
 * which hashes it with bcrypt before storing it (see kasi-api/models/User.js) —
 * the plain-text password is never written to disk on the device or kept in memory
 * beyond the single request.
 */
class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    suspend fun register(name: String, email: String, password: String, phone: String?): Result<Unit> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password, phone))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                sessionManager.saveSession(body.token, body.user.id, body.user.name)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                sessionManager.saveSession(body.token, body.user.id, body.user.name)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
