package com.kasimarketplace.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasimarketplace.app.data.repository.CartLine
import com.kasimarketplace.app.data.repository.CartRepository
import com.kasimarketplace.app.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CartViewModel"

sealed class CheckoutState {
    data object Idle : CheckoutState()
    data object Placing : CheckoutState()
    data object Success : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartLine>> = cartRepository.items

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()

    val subtotal: Double
        get() = cartRepository.subtotal

    fun removeItem(productId: String) {
        cartRepository.removeFromCart(productId)
    }

    fun checkout() {
        val items = cartItems.value
        if (items.isEmpty()) {
            _checkoutState.value = CheckoutState.Error("Your cart is empty.")
            return
        }
        _checkoutState.value = CheckoutState.Placing
        viewModelScope.launch {
            val result = orderRepository.placeOrder(items)
            _checkoutState.value = result.fold(
                onSuccess = {
                    Log.i(TAG, "Order ${it.id} placed successfully")
                    cartRepository.clear()
                    CheckoutState.Success
                },
                onFailure = {
                    Log.e(TAG, "Checkout failed: ${it.message}")
                    CheckoutState.Error(it.message ?: "Could not place order. Please try again.")
                }
            )
        }
    }
}
