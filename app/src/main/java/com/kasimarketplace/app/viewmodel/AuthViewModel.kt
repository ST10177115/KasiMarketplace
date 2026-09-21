package com.kasimarketplace.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasimarketplace.app.data.repository.AuthRepository
import com.kasimarketplace.app.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

/**
 * Handles both the Register and Login screens' form state, delegates client-side input
 * validation to Validators (so the app never crashes or sends obviously invalid input to
 * the API), and triggers the resulting network call.
 */
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String, phone: String?) {
        val validationError = Validators.validateRegistration(name, email, password)
        if (validationError != null) {
            Log.w(TAG, "Registration blocked by client-side validation: $validationError")
            _uiState.value = AuthUiState.Error(validationError)
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = authRepository.register(name.trim(), email.trim(), password, phone?.trim())
            _uiState.value = result.fold(
                onSuccess = {
                    Log.i(TAG, "Registration successful for $email")
                    AuthUiState.Success
                },
                onFailure = {
                    Log.e(TAG, "Registration failed: ${it.message}")
                    AuthUiState.Error(it.message ?: "Registration failed. Please try again.")
                }
            )
        }
    }

    fun login(email: String, password: String) {
        val validationError = Validators.validateLogin(email, password)
        if (validationError != null) {
            _uiState.value = AuthUiState.Error(validationError)
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = authRepository.login(email.trim(), password)
            _uiState.value = result.fold(
                onSuccess = {
                    Log.i(TAG, "Login successful for $email")
                    AuthUiState.Success
                },
                onFailure = {
                    Log.e(TAG, "Login failed: ${it.message}")
                    AuthUiState.Error(it.message ?: "Invalid email or password.")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
