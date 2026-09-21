package com.kasimarketplace.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasimarketplace.app.ui.components.KasiErrorText
import com.kasimarketplace.app.ui.components.KasiLoadingIndicator
import com.kasimarketplace.app.ui.components.KasiPrimaryButton
import com.kasimarketplace.app.ui.components.KasiTextField
import com.kasimarketplace.app.viewmodel.SettingsViewModel

/**
 * Lets the logged-in user view and update their settings (Requirement 3 from Part 1):
 * name, phone number, and notification preferences. Also provides logout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var notificationsEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.user) {
        uiState.user?.let {
            name = it.name
            phone = it.phone ?: ""
            notificationsEnabled = it.notificationsEnabled
        }
    }

    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) onLoggedOut()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            KasiLoadingIndicator(modifier = Modifier.padding(padding))
            return@Scaffold
        }

        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp)) {
            Text("Account details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            KasiTextField(value = name, onValueChange = { name = it }, label = "Full name")
            Spacer(modifier = Modifier.height(12.dp))
            KasiTextField(value = phone, onValueChange = { phone = it }, label = "Phone number")

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Notifications", style = MaterialTheme.typography.titleMedium)
                Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
            }

            Spacer(modifier = Modifier.height(8.dp))
            uiState.errorMessage?.let { KasiErrorText(it) }
            uiState.saveMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(20.dp))
            KasiPrimaryButton(
                text = "Save Settings",
                onClick = { viewModel.saveSettings(name, phone, notificationsEnabled) }
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }
        }
    }
}
