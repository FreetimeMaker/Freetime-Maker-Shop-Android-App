package com.freetime.data.repository

import com.freetime.domain.model.Product
import com.freetime.domain.model.CartItem
import com.freetime.domain.model.Order
import com.freetime.domain.model.OrderStatus
import com.freetime.domain.model.SampleProducts
import com.freetime.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.math.round

class ProductRepositoryImpl : ProductRepository {
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    
    override suspend fun getProducts(): Result<List<Product>> {
        return Result.success(SampleProducts.products)
    }
    
    override suspend fun getProductById(id: String): Result<Product?> {
        val product = SampleProducts.products.find { it.id == id }
        return Result.success(product)
    }
    
    override suspend fun getProductsByCategory(category: com.freetime.domain.model.ProductCategory): Result<List<Product>> {
        val filteredProducts = SampleProducts.products.filter { it.category == category }
        return Result.success(filteredProducts)
    }
    
    override suspend fun getProductsByPlatform(platform: com.freetime.domain.model.Platform): Result<List<Product>> {
        val filteredProducts = SampleProducts.products.filter { it.platform == platform }
        return Result.success(filteredProducts)
    }
    
    override fun getCartItems(): Flow<List<CartItem>> = _cartItems.asStateFlow()
    
    override suspend fun addToCart(product: Product, quantity: Int): Result<Unit> {
        return try {
            val currentCart = _cartItems.value.toMutableList()
            val existingItemIndex = currentCart.indexOfFirst { it.product.id == product.id }
            
            if (existingItemIndex >= 0) {
                val existingItem = currentCart[existingItemIndex]
                currentCart[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
            } else {
                currentCart.add(CartItem(product, quantity))
            }
            
            _cartItems.value = currentCart
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removeFromCart(productId: String): Result<Unit> {
        return try {
            val currentCart = _cartItems.value.toMutableList()
            currentCart.removeAll { it.product.id == productId }
            _cartItems.value = currentCart
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCartQuantity(productId: String, quantity: Int): Result<Unit> {
        return try {
            if (quantity <= 0) {
                return removeFromCart(productId)
            }
            
            val currentCart = _cartItems.value.toMutableList()
            val itemIndex = currentCart.indexOfFirst { it.product.id == productId }
            
            if (itemIndex >= 0) {
                val item = currentCart[itemIndex]
                currentCart[itemIndex] = item.copy(quantity = quantity)
                _cartItems.value = currentCart
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clearCart(): Result<Unit> {
        return try {
            _cartItems.value = emptyList()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createOrder(items: List<CartItem>, customerEmail: String): Result<Order> {
        return try {
            val totalAmount = items.sumOf { it.product.price * it.quantity }
            val order = Order(
                id = UUID.randomUUID().toString(),
                items = items,
                totalAmount = round(totalAmount * 100) / 100, // Round to 2 decimal places
                currency = "USD",
                status = OrderStatus.PENDING,
                createdAt = System.currentTimeMillis(),
                customerEmail = customerEmail
            )
            
            val currentOrders = _orders.value.toMutableList()
            currentOrders.add(order)
            _orders.value = currentOrders
            
            // Clear cart after successful order creation
            clearCart()
            
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOrderHistory(): Result<List<Order>> {
        return Result.success(_orders.value.sortedByDescending { it.createdAt })
    }
    
    override suspend fun getCartTotal(): Result<Double> {
        val total = _cartItems.value.sumOf { it.product.price * it.quantity }
        return Result.success(round(total * 100) / 100)
    }
}
