package com.kasimarketplace.app

import com.google.common.truth.Truth.assertThat
import com.kasimarketplace.app.util.Validators
import org.junit.Test

/**
 * Unit tests for client-side input validation used before any registration or login
 * request is sent to the REST API. These run on the JVM (no emulator required) via
 * ./gradlew test, and are also run automatically by the GitHub Actions workflow.
 */
class ValidatorsTest {

    @Test
    fun `valid email passes validation`() {
        assertThat(Validators.isValidEmail("banathi@example.com")).isTrue()
    }

    @Test
    fun `email without at symbol fails validation`() {
        assertThat(Validators.isValidEmail("banathi.example.com")).isFalse()
    }

    @Test
    fun `email without domain fails validation`() {
        assertThat(Validators.isValidEmail("banathi@")).isFalse()
    }

    @Test
    fun `password with 8 or more characters is valid`() {
        assertThat(Validators.isValidPassword("password123")).isTrue()
    }

    @Test
    fun `password shorter than 8 characters is invalid`() {
        assertThat(Validators.isValidPassword("short")).isFalse()
    }

    @Test
    fun `registration validation fails when name is blank`() {
        val error = Validators.validateRegistration("", "test@example.com", "password123")
        assertThat(error).isEqualTo("Please enter your name.")
    }

    @Test
    fun `registration validation fails on invalid email`() {
        val error = Validators.validateRegistration("Banathi", "not-an-email", "password123")
        assertThat(error).isEqualTo("Please enter a valid email address.")
    }

    @Test
    fun `registration validation fails on short password`() {
        val error = Validators.validateRegistration("Banathi", "test@example.com", "123")
        assertThat(error).isEqualTo("Password must be at least 8 characters.")
    }

    @Test
    fun `registration validation passes with valid input`() {
        val error = Validators.validateRegistration("Banathi", "test@example.com", "password123")
        assertThat(error).isNull()
    }

    @Test
    fun `login validation fails when email is blank`() {
        val error = Validators.validateLogin("", "password123")
        assertThat(error).isEqualTo("Please enter your email.")
    }

    @Test
    fun `login validation fails when password is blank`() {
        val error = Validators.validateLogin("test@example.com", "")
        assertThat(error).isEqualTo("Please enter your password.")
    }

    @Test
    fun `login validation passes with valid input`() {
        val error = Validators.validateLogin("test@example.com", "password123")
        assertThat(error).isNull()
    }
}
