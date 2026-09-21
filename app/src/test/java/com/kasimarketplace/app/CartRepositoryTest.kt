package com.kasimarketplace.app

import com.google.common.truth.Truth.assertThat
import com.kasimarketplace.app.data.model.Product
import com.kasimarketplace.app.data.repository.CartRepository
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for CartRepository, covering adding, merging quantities, removing, and
 * subtotal calculation — the core cart logic used by the Cart screen and CartViewModel.
 */
class CartRepositoryTest {

    private lateinit var cartRepository: CartRepository

    private val sampleProduct = Product(
        id = "p1", vendorId = "v1", vendorName = "Test Vendor", name = "Test Product",
        description = "A test product", price = 100.0, category = "Test", stockQty = 10
    )

    private val secondProduct = sampleProduct.copy(id = "p2", name = "Second Product", price = 50.0)

    @Before
    fun setUp() {
        cartRepository = CartRepository()
    }

    @Test
    fun `adding a new product creates a cart line with quantity 1`() {
        cartRepository.addToCart(sampleProduct)
        assertThat(cartRepository.items.value).hasSize(1)
        assertThat(cartRepository.items.value.first().quantity).isEqualTo(1)
    }

    @Test
    fun `adding the same product twice increments quantity instead of duplicating`() {
        cartRepository.addToCart(sampleProduct)
        cartRepository.addToCart(sampleProduct)
        assertThat(cartRepository.items.value).hasSize(1)
        assertThat(cartRepository.items.value.first().quantity).isEqualTo(2)
    }

    @Test
    fun `subtotal reflects price times quantity across multiple products`() {
        cartRepository.addToCart(sampleProduct) // 100.0 x 1
        cartRepository.addToCart(secondProduct) // 50.0 x 1
        cartRepository.addToCart(secondProduct) // 50.0 x 2 total
        assertThat(cartRepository.subtotal).isEqualTo(200.0) // 100 + (50*2)
    }

    @Test
    fun `removing a product removes it from the cart`() {
        cartRepository.addToCart(sampleProduct)
        cartRepository.addToCart(secondProduct)
        cartRepository.removeFromCart(sampleProduct.id)
        assertThat(cartRepository.items.value).hasSize(1)
        assertThat(cartRepository.items.value.first().product.id).isEqualTo(secondProduct.id)
    }

    @Test
    fun `clear empties the cart`() {
        cartRepository.addToCart(sampleProduct)
        cartRepository.addToCart(secondProduct)
        cartRepository.clear()
        assertThat(cartRepository.items.value).isEmpty()
        assertThat(cartRepository.subtotal).isEqualTo(0.0)
    }
}
