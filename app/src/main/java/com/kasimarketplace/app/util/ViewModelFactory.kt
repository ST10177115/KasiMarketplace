package com.kasimarketplace.app.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kasimarketplace.app.viewmodel.*

/**
 * Single factory that knows how to build every ViewModel in the app from the shared
 * AppContainer, since these ViewModels take repository dependencies in their constructors
 * rather than using a DI framework like Hilt.
 */
class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(container.authRepository) as T
            ProductViewModel::class.java -> ProductViewModel(container.productRepository) as T
            ProductDetailViewModel::class.java -> ProductDetailViewModel(container.productRepository, container.cartRepository) as T
            CartViewModel::class.java -> CartViewModel(container.cartRepository, container.orderRepository) as T
            SettingsViewModel::class.java -> SettingsViewModel(container.userRepository, container.authRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
