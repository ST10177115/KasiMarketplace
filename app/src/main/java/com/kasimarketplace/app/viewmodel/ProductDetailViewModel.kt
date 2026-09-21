package com.kasimarketplace.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasimarketplace.app.data.model.Product
import com.kasimarketplace.app.data.repository.CartRepository
import com.kasimarketplace.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val addedToCart: Boolean = false,
    val errorMessage: String? = null
)

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            val result = productRepository.getProduct(productId)
            _uiState.value = result.fold(
                onSuccess = { ProductDetailUiState(isLoading = false, product = it) },
                onFailure = { ProductDetailUiState(isLoading = false, errorMessage = "Could not load this product.") }
            )
        }
    }

    fun addToCart() {
        val product = _uiState.value.product ?: return
        cartRepository.addToCart(product)
        _uiState.value = _uiState.value.copy(addedToCart = true)
    }
}
