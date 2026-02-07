package com.freetime.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetime.domain.model.CartItem
import com.freetime.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CartViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CartUIState>(CartUIState.Loading)
    val uiState: StateFlow<CartUIState> = _uiState.asStateFlow()
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val _cartTotal = MutableStateFlow(0.0)
    val cartTotal: StateFlow<Double> = _cartTotal.asStateFlow()
    
    init {
        loadCart()
    }
    
    private fun loadCart() {
        viewModelScope.launch {
            try {
                productRepository.getCartItems().collect { items ->
                    _cartItems.value = items
                    val total = productRepository.getCartTotal().getOrNull() ?: 0.0
                    _cartTotal.value = total
                    
                    _uiState.value = if (items.isEmpty()) {
                        CartUIState.Empty
                    } else {
                        CartUIState.Success(items, total)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CartUIState.Error(e.message ?: "Failed to load cart")
            }
        }
    }
    
    fun addToCart(product: com.freetime.domain.model.Product, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                productRepository.addToCart(product, quantity)
            } catch (e: Exception) {
                _uiState.value = CartUIState.Error(e.message ?: "Failed to add to cart")
            }
        }
    }
    
    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                productRepository.removeFromCart(productId)
            } catch (e: Exception) {
                _uiState.value = CartUIState.Error(e.message ?: "Failed to remove from cart")
            }
        }
    }
    
    fun updateQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                productRepository.updateCartQuantity(productId, quantity)
            } catch (e: Exception) {
                _uiState.value = CartUIState.Error(e.message ?: "Failed to update quantity")
            }
        }
    }
    
    fun clearCart() {
        viewModelScope.launch {
            try {
                productRepository.clearCart()
            } catch (e: Exception) {
                _uiState.value = CartUIState.Error(e.message ?: "Failed to clear cart")
            }
        }
    }
    
    fun refreshCart() {
        loadCart()
    }
}

sealed class CartUIState {
    object Loading : CartUIState()
    object Empty : CartUIState()
    data class Success(val items: List<CartItem>, val total: Double) : CartUIState()
    data class Error(val message: String) : CartUIState()
}
