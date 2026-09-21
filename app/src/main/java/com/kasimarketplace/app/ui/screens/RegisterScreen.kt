package com.kasimarketplace.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kasimarketplace.app.ui.components.*
import com.kasimarketplace.app.viewmodel.AuthUiState
import com.kasimarketplace.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "\u2190 Back",
            modifier = Modifier.clickable { onNavigateBack() },
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Create your account", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Join Kasi Marketplace to buy and sell locally", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        KasiTextField(value = name, onValueChange = { name = it }, label = "Full name")
        Spacer(modifier = Modifier.height(12.dp))
        KasiTextField(value = email, onValueChange = { email = it }, label = "Email", keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(12.dp))
        KasiTextField(value = phone, onValueChange = { phone = it }, label = "Phone number (optional)", keyboardType = KeyboardType.Phone)
        Spacer(modifier = Modifier.height(12.dp))
        KasiTextField(value = password, onValueChange = { password = it }, label = "Password (min. 8 characters)", isPassword = true)
        Spacer(modifier = Modifier.height(20.dp))

        if (uiState is AuthUiState.Error) {
            KasiErrorText((uiState as AuthUiState.Error).message)
            Spacer(modifier = Modifier.height(12.dp))
        }

        KasiPrimaryButton(
            text = if (uiState is AuthUiState.Loading) "Creating account..." else "Register",
            onClick = { viewModel.register(name, email, password, phone.ifBlank { null }) },
            enabled = uiState !is AuthUiState.Loading
        )
    }
}
