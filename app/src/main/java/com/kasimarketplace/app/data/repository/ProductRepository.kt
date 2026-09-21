package com.kasimarketplace.app.data.repository

import com.kasimarketplace.app.data.local.ProductDao
import com.kasimarketplace.app.data.local.toCachedEntity
import com.kasimarketplace.app.data.local.toProduct
import com.kasimarketplace.app.data.model.Product
import com.kasimarketplace.app.data.remote.ApiService
import kotlinx.coroutines.flow.first

/**
 * Fetches product listings from the REST API when a connection is available, and falls
 * back to the last-cached listings stored in Room when the network call fails.
 * Every successful fetch refreshes the cache, so the most recent listings are always
 * what a user sees offline.
 */
class ProductRepository(
    private val apiService: ApiService,
    private val productDao: ProductDao
) {

    suspend fun getProducts(category: String? = null, search: String? = null): Result<List<Product>> {
        return try {
            val response = apiService.getProducts(category, search)
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!
                productDao.insertAll(products.map { it.toCachedEntity() })
                Result.success(products)
            } else {
                Result.success(getCachedProducts())
            }
        } catch (e: Exception) {
            // No connectivity or the API is unreachable — fall back to what was cached locally.
            Result.success(getCachedProducts())
        }
    }

    private suspend fun getCachedProducts(): List<Product> =
        productDao.observeAll().first().map { it.toProduct() }

    suspend fun getProduct(id: String): Result<Product> {
        return try {
            val response = apiService.getProduct(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val cached = productDao.observeAll().first().find { it.id == id }
                if (cached != null) Result.success(cached.toProduct())
                else Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            val cached = productDao.observeAll().first().find { it.id == id }
            if (cached != null) Result.success(cached.toProduct())
            else Result.failure(e)
        }
    }
}
