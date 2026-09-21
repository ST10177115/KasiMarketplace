package com.kasimarketplace.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasimarketplace.app.data.model.Product
import com.kasimarketplace.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ProductViewModel"

data class ProductListUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = productRepository.getProducts(search = _uiState.value.searchQuery.ifBlank { null })
            _uiState.value = result.fold(
                onSuccess = { products ->
                    Log.i(TAG, "Loaded ${products.size} products")
                    _uiState.value.copy(isLoading = false, products = products)
                },
                onFailure = {
                    Log.e(TAG, "Failed to load products: ${it.message}")
                    _uiState.value.copy(isLoading = false, errorMessage = "Could not refresh listings. Showing saved results.")
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadProducts()
    }
}
