package com.kasimarketplace.app.data.remote

import com.kasimarketplace.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Defines every REST endpoint the Android app calls on the Kasi Marketplace API.
 * Base URL is configured in RetrofitClient (BuildConfig.BASE_URL).
 * All endpoints except register/login require the Authorization: Bearer <token> header,
 * which AuthInterceptor attaches automatically (see RetrofitClient.kt).
 */
interface ApiService {

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/v1/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<AuthResponse>

    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<User>

    @PATCH("api/v1/users/me")
    suspend fun updateSettings(@Body request: SettingsUpdateRequest): Response<User>

    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<List<Product>>

    @GET("api/v1/products/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<Product>

    @POST("api/v1/products")
    suspend fun createProduct(@Body request: ProductCreateRequest): Response<Product>

    @POST("api/v1/orders")
    suspend fun placeOrder(@Body request: OrderCreateRequest): Response<Order>

    @GET("api/v1/orders")
    suspend fun getMyOrders(): Response<List<Order>>
}
