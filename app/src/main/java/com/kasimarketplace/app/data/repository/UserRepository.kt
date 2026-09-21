package com.kasimarketplace.app.data.repository

import com.kasimarketplace.app.data.model.SettingsUpdateRequest
import com.kasimarketplace.app.data.model.User
import com.kasimarketplace.app.data.remote.ApiService

/** Handles fetching and updating the logged-in user's profile and settings. */
class UserRepository(private val apiService: ApiService) {

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Could not load profile"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSettings(name: String, phone: String?, notificationsEnabled: Boolean): Result<User> {
        return try {
            val response = apiService.updateSettings(SettingsUpdateRequest(name, phone, notificationsEnabled))
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Could not update settings"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
