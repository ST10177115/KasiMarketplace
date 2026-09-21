package com.kasimarketplace.app.data.model

/**
 * Represents a product listing fetched from the REST API.
 * See kasi-api/models/Product.js for the matching server-side schema.
 */
data class Product(
    val id: String,
    val vendorId: String,
    val vendorName: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val stockQty: Int,
    val imageUrl: String? = null
)

/** Sent to POST /api/v1/products when a vendor creates a new listing. */
data class ProductCreateRequest(
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val stockQty: Int
)
