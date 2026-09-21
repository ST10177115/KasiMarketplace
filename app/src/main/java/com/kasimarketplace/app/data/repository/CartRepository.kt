package com.kasimarketplace.app.data.repository

import com.kasimarketplace.app.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CartLine(val product: Product, val quantity: Int)

/**
 * Holds the buyer's cart in memory for the lifetime of the app process.
 * A single shared instance (see AppContainer) so Home, Product Detail, and Cart
 * screens all observe the same cart state.
 */
class CartRepository {

    private val _items = MutableStateFlow<List<CartLine>>(emptyList())
    val items: StateFlow<List<CartLine>> = _items.asStateFlow()

    val subtotal: Double
        get() = _items.value.sumOf { it.product.price * it.quantity }

    fun addToCart(product: Product) {
        val current = _items.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.product.id == product.id }
        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartLine(product, 1))
        }
        _items.value = current
    }

    fun removeFromCart(productId: String) {
        _items.value = _items.value.filterNot { it.product.id == productId }
    }

    fun clear() {
        _items.value = emptyList()
    }
}
