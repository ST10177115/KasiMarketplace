package com.kasimarketplace.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasimarketplace.app.ui.components.KasiLoadingIndicator
import com.kasimarketplace.app.ui.components.KasiPrimaryButton
import com.kasimarketplace.app.viewmodel.ProductDetailViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductDetailViewModel,
    onNavigateBack: () -> Unit,
    onGoToCart: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading -> KasiLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.errorMessage != null -> Text(
                    uiState.errorMessage!!,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                uiState.product != null -> {
                    val product = uiState.product!!
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "R %.2f", product.price),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (product.stockQty > 0) "In stock (${product.stockQty} available)" else "Out of stock",
                            color = if (product.stockQty > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sold by ${product.vendorName}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(product.description, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(32.dp))

                        if (uiState.addedToCart) {
                            KasiPrimaryButton(text = "View Cart", onClick = onGoToCart)
                        } else {
                            KasiPrimaryButton(
                                text = "Add to Cart",
                                onClick = { viewModel.addToCart() },
                                enabled = product.stockQty > 0
                            )
                        }
                    }
                }
            }
        }
    }
}
