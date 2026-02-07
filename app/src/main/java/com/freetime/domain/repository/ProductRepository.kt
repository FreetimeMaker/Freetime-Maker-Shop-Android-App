package com.freetime.domain.repository

import com.freetime.domain.model.Product
import com.freetime.domain.model.CartItem
import com.freetime.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product?>
    suspend fun getProductsByCategory(category: com.freetime.domain.model.ProductCategory): Result<List<Product>>
    suspend fun getProductsByPlatform(platform: com.freetime.domain.model.Platform): Result<List<Product>>
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(product: Product, quantity: Int = 1): Result<Unit>
    suspend fun removeFromCart(productId: String): Result<Unit>
    suspend fun updateCartQuantity(productId: String, quantity: Int): Result<Unit>
    suspend fun clearCart(): Result<Unit>
    suspend fun createOrder(items: List<CartItem>, customerEmail: String): Result<Order>
    suspend fun getOrderHistory(): Result<List<Order>>
    suspend fun getCartTotal(): Result<Double>
}
