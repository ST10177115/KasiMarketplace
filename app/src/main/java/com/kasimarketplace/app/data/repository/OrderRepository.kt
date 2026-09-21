package com.kasimarketplace.app.data.repository

import com.kasimarketplace.app.data.model.Order
import com.kasimarketplace.app.data.model.OrderCreateRequest
import com.kasimarketplace.app.data.model.OrderItemRequest
import com.kasimarketplace.app.data.remote.ApiService

class OrderRepository(private val apiService: ApiService) {

    suspend fun placeOrder(cartLines: List<CartLine>): Result<Order> {
        return try {
            val request = OrderCreateRequest(
                items = cartLines.map { OrderItemRequest(it.product.id, it.quantity) }
            )
            val response = apiService.placeOrder(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Could not place order"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
