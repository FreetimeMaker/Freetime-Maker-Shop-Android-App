package com.freetime.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetime.domain.usecase.CheckoutUseCase
import com.freetime.domain.usecase.CheckoutResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val checkoutUseCase: CheckoutUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CheckoutUIState>(CheckoutUIState.Idle)
    val uiState: StateFlow<CheckoutUIState> = _uiState.asStateFlow()
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()
    
    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }
    
    fun performCheckout() {
        val currentEmail = _email.value
        if (currentEmail.isBlank()) {
            _uiState.value = CheckoutUIState.Error("Email is required")
            return
        }
        
        if (!isValidEmail(currentEmail)) {
            _uiState.value = CheckoutUIState.Error("Please enter a valid email address")
            return
        }
        
        viewModelScope.launch {
            try {
                _isProcessing.value = true
                _uiState.value = CheckoutUIState.Processing
                
                val result = checkoutUseCase(currentEmail)
                
                if (result.isSuccess) {
                    val checkoutResult = result.getOrNull()!!
                    when (checkoutResult) {
                        is CheckoutResult.Success -> {
                            _uiState.value = CheckoutUIState.Success(
                                order = checkoutResult.order,
                                message = "Payment successful! Order #${checkoutResult.order.id}"
                            )
                        }
                        is CheckoutResult.PaymentFailed -> {
                            _uiState.value = CheckoutUIState.Error(
                                checkoutResult.errorMessage
                            )
                        }
                        is CheckoutResult.PaymentPending -> {
                            _uiState.value = CheckoutUIState.Pending(
                                order = checkoutResult.order,
                                message = "Payment is being processed..."
                            )
                        }
                    }
                } else {
                    _uiState.value = CheckoutUIState.Error(
                        result.exceptionOrNull()?.message ?: "Checkout failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CheckoutUIState.Error(e.message ?: "Checkout failed")
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    fun resetState() {
        _uiState.value = CheckoutUIState.Idle
        _email.value = ""
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

sealed class CheckoutUIState {
    object Idle : CheckoutUIState()
    object Processing : CheckoutUIState()
    data class Success(val order: com.freetime.domain.model.Order, val message: String) : CheckoutUIState()
    data class Pending(val order: com.freetime.domain.model.Order, val message: String) : CheckoutUIState()
    data class Error(val message: String) : CheckoutUIState()
}
