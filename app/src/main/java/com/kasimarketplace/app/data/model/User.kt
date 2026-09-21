package com.kasimarketplace.app.data.model

/**
 * Represents a registered user of Kasi Marketplace.
 * The password itself is never held here on the client after registration/login;
 * only the server stores the bcrypt hash (see kasi-api/models/User.js).
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val notificationsEnabled: Boolean = true
)

/** Sent to POST /api/v1/auth/register */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null
)

/** Sent to POST /api/v1/auth/login */
data class LoginRequest(
    val email: String,
    val password: String
)

/** Returned by both register and login on success. Contains the JWT used for all further calls. */
data class AuthResponse(
    val token: String,
    val user: User
)

/** Sent to PUT /api/v1/users/settings to update a user's editable settings. */
data class SettingsUpdateRequest(
    val name: String,
    val phone: String?,
    val notificationsEnabled: Boolean
)
