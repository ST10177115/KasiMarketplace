package com.kasimarketplace.app.util

/**
 * Pure, framework-free validation logic used by AuthViewModel before any input is sent
 * to the API. Kept separate (no Android dependencies) so it can be unit tested directly
 * on the JVM without needing an emulator — see app/src/test/.../ValidatorsTest.kt.
 */
object Validators {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isValidPassword(password: String): Boolean = password.length >= 8

    /** Returns an error message describing the first problem found, or null if valid. */
    fun validateRegistration(name: String, email: String, password: String): String? {
        return when {
            name.isBlank() -> "Please enter your name."
            !isValidEmail(email) -> "Please enter a valid email address."
            !isValidPassword(password) -> "Password must be at least 8 characters."
            else -> null
        }
    }

    fun validateLogin(email: String, password: String): String? {
        return when {
            email.isBlank() -> "Please enter your email."
            password.isBlank() -> "Please enter your password."
            else -> null
        }
    }
}
