package com.freetime.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetime.domain.model.Product
import com.freetime.domain.model.ProductCategory
import com.freetime.domain.model.Platform
import com.freetime.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProductUIState>(ProductUIState.Loading)
    val uiState: StateFlow<ProductUIState> = _uiState.asStateFlow()
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory: StateFlow<ProductCategory?> = _selectedCategory.asStateFlow()
    
    private val _selectedPlatform = MutableStateFlow<Platform?>(null)
    val selectedPlatform: StateFlow<Platform?> = _selectedPlatform.asStateFlow()
    
    init {
        loadProducts()
    }
    
    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _uiState.value = ProductUIState.Loading
                val result = productRepository.getProducts()
                if (result.isSuccess) {
                    val products = result.getOrNull() ?: emptyList()
                    _products.value = products
                    _filteredProducts.value = products
                    _uiState.value = ProductUIState.Success(products)
                } else {
                    _uiState.value = ProductUIState.Error(result.exceptionOrNull()?.message ?: "Failed to load products")
                }
            } catch (e: Exception) {
                _uiState.value = ProductUIState.Error(e.message ?: "Failed to load products")
            }
        }
    }
    
    fun filterByCategory(category: ProductCategory?) {
        _selectedCategory.value = category
        applyFilters()
    }
    
    fun filterByPlatform(platform: Platform?) {
        _selectedPlatform.value = platform
        applyFilters()
    }
    
    fun clearFilters() {
        _selectedCategory.value = null
        _selectedPlatform.value = null
        _filteredProducts.value = _products.value
    }
    
    private fun applyFilters() {
        val category = _selectedCategory.value
        val platform = _selectedPlatform.value
        
        val filtered = _products.value.filter { product ->
            val categoryMatch = category == null || product.category == category
            val platformMatch = platform == null || product.platform == platform || product.platform == Platform.ALL
            categoryMatch && platformMatch
        }
        
        _filteredProducts.value = filtered
    }
    
    fun getProductById(id: String): Product? {
        return _products.value.find { it.id == id }
    }
    
    fun refreshProducts() {
        loadProducts()
    }
}

sealed class ProductUIState {
    object Loading : ProductUIState()
    data class Success(val products: List<Product>) : ProductUIState()
    data class Error(val message: String) : ProductUIState()
}
