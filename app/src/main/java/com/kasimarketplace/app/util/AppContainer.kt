package com.kasimarketplace.app.util

import android.content.Context
import com.kasimarketplace.app.data.local.AppDatabase
import com.kasimarketplace.app.data.local.SessionManager
import com.kasimarketplace.app.data.remote.RetrofitClient
import com.kasimarketplace.app.data.repository.*

/**
 * Lightweight manual dependency container. Built once in MainActivity and passed down
 * to ViewModels, avoiding the need for a full DI framework in this prototype.
 */
class AppContainer(context: Context) {
    val sessionManager = SessionManager(context)
    private val apiService = RetrofitClient.create(sessionManager)
    private val database = AppDatabase.getInstance(context)

    val authRepository = AuthRepository(apiService, sessionManager)
    val userRepository = UserRepository(apiService)
    val productRepository = ProductRepository(apiService, database.productDao())
    val cartRepository = CartRepository()
    val orderRepository = OrderRepository(apiService)
}
