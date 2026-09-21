package com.kasimarketplace.app.data.model

import com.google.gson.annotations.SerializedName

/** A single line item within an order, mirrors an item in the on-device cart. */
data class OrderItem(
    val productId: String,
    // The API stores/returns this field as "name" (a snapshot of the product name
    // at the time of purchase) — mapped here to the more descriptive Kotlin name.
    @SerializedName("name") val productName: String,
    val quantity: Int,
    val unitPrice: Double
)

/** Sent to POST /api/v1/orders when the buyer checks out. */
data class OrderCreateRequest(
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val productId: String,
    val quantity: Int
)

/** Returned by the API after an order is placed or fetched. */
data class Order(
    val id: String,
    val buyerId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: String, // "Pending", "Confirmed", "Delivered", "Cancelled"
    val createdAt: String
)
