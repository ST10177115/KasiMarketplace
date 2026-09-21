package com.kasimarketplace.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasimarketplace.app.data.model.User
import com.kasimarketplace.app.data.repository.AuthRepository
import com.kasimarketplace.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "SettingsViewModel"

data class SettingsUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val saveMessage: String? = null,
    val errorMessage: String? = null,
    val loggedOut: Boolean = false
)

/**
 * Backs the Settings screen: loads the current user's profile, saves edits back to the
 * API (Requirement 3 from Part 1 — user must be able to change their settings), and
 * handles logout.
 */
class SettingsViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUser()
            _uiState.value = result.fold(
                onSuccess = { SettingsUiState(isLoading = false, user = it) },
                onFailure = { SettingsUiState(isLoading = false, errorMessage = "Could not load your profile.") }
            )
        }
    }

    fun saveSettings(name: String, phone: String?, notificationsEnabled: Boolean) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Name cannot be empty.")
            return
        }
        viewModelScope.launch {
            val result = userRepository.updateSettings(name.trim(), phone?.trim(), notificationsEnabled)
            _uiState.value = result.fold(
                onSuccess = {
                    Log.i(TAG, "Settings updated successfully")
                    _uiState.value.copy(user = it, saveMessage = "Settings saved.", errorMessage = null)
                },
                onFailure = {
                    Log.e(TAG, "Failed to save settings: ${it.message}")
                    _uiState.value.copy(errorMessage = "Could not save settings. Please try again.")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = _uiState.value.copy(loggedOut = true)
        }
    }
}
