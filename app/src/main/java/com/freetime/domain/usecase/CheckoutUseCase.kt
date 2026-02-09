package com.freetime.domain.usecase

import com.freetime.domain.model.Order
import com.freetime.domain.model.OrderStatus
import com.freetime.domain.payment.FreetimePaymentManager
import com.freetime.domain.payment.PaymentResult
import com.freetime.domain.payment.PaymentStatus
import com.freetime.domain.repository.ProductRepository

class CheckoutUseCase(
    private val productRepository: ProductRepository,
    private val paymentManager: FreetimePaymentManager
) {
    
    suspend operator fun invoke(customerEmail: String): Result<CheckoutResult> {
        return try {
            // Get cart items
            val cartItems = mutableListOf<com.freetime.domain.model.CartItem>()
            productRepository.getCartItems().collect { items ->
                cartItems.clear()
                cartItems.addAll(items)
            }
            
            if (cartItems.isEmpty()) {
                return Result.failure(Exception("Cart is empty"))
            }
            
            // Create order
            val orderResult = productRepository.createOrder(cartItems, customerEmail)
            if (orderResult.isFailure) {
                return Result.failure(orderResult.exceptionOrNull() ?: Exception("Failed to create order"))
            }
            
            val order = orderResult.getOrNull()!!
            
            // Initialize payment
            val paymentSessionResult = paymentManager.initializePayment(
                amount = order.totalAmount,
                currency = order.currency,
                orderId = order.id,
                customerEmail = customerEmail,
                description = "Order ${order.id} - ${order.items.size} items"
            )
            
            if (paymentSessionResult.isFailure) {
                return Result.failure(paymentSessionResult.exceptionOrNull() ?: Exception("Failed to initialize payment"))
            }
            
            val paymentSession = paymentSessionResult.getOrNull()!!
            
            // Process payment
            val paymentResult = paymentManager.processPayment(paymentSession)
            
            if (paymentResult.isFailure) {
                return Result.failure(paymentResult.exceptionOrNull() ?: Exception("Payment processing failed"))
            }
            
            val result = paymentResult.getOrNull()!!
            
            val checkoutResult = when (result.status) {
                PaymentStatus.COMPLETED -> {
                    CheckoutResult.Success(
                        order = order.copy(
                            status = OrderStatus.PAID,
                            paymentId = result.paymentId
                        ),
                        paymentResult = result
                    )
                }
                PaymentStatus.FAILED -> {
                    CheckoutResult.PaymentFailed(
                        order = order,
                        errorMessage = result.errorMessage ?: "Payment failed"
                    )
                }
                else -> {
                    CheckoutResult.PaymentPending(
                        order = order,
                        paymentSession = paymentSession
                    )
                }
            }
            
            Result.success(checkoutResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

sealed class CheckoutResult {
    data class Success(
        val order: Order,
        val paymentResult: PaymentResult
    ) : CheckoutResult()
    
    data class PaymentFailed(
        val order: Order,
        val errorMessage: String
    ) : CheckoutResult()
    
    data class PaymentPending(
        val order: Order,
        val paymentSession: com.freetime.domain.payment.PaymentSession
    ) : CheckoutResult()
}
