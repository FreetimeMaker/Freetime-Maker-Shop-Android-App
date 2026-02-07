package com.freetime.data.payment

import com.freetime.domain.payment.FreetimePaymentManager
import com.freetime.domain.payment.PaymentSession
import com.freetime.domain.payment.PaymentResult
import com.freetime.domain.payment.PaymentStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.random.Random

class FreetimePaymentManagerImpl : FreetimePaymentManager {
    
    private val _paymentStatuses = MutableStateFlow<Map<String, PaymentStatus>>(emptyMap())
    
    override suspend fun initializePayment(
        amount: Double,
        currency: String,
        orderId: String,
        customerEmail: String,
        description: String
    ): Result<PaymentSession> {
        return try {
            val paymentId = UUID.randomUUID().toString()
            val expiresAt = System.currentTimeMillis() + (30 * 60 * 1000) // 30 minutes
            
            val paymentSession = PaymentSession(
                paymentId = paymentId,
                amount = amount,
                currency = currency,
                merchantId = "freetime_maker_shop",
                customerEmail = customerEmail,
                description = description,
                paymentUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/payment/$paymentId",
                expiresAt = expiresAt,
                status = PaymentStatus.PENDING
            )
            
            // Update payment status
            val currentStatuses = _paymentStatuses.value.toMutableMap()
            currentStatuses[paymentId] = PaymentStatus.PENDING
            _paymentStatuses.value = currentStatuses
            
            Result.success(paymentSession)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun processPayment(paymentSession: PaymentSession): Result<PaymentResult> {
        return try {
            // Simulate payment processing
            updatePaymentStatus(paymentSession.paymentId, PaymentStatus.PROCESSING)
            delay(2000) // Simulate network delay
            
            // Simulate success/failure (90% success rate for demo)
            val isSuccess = Random.nextDouble() > 0.1
            
            val status = if (isSuccess) {
                updatePaymentStatus(paymentSession.paymentId, PaymentStatus.COMPLETED)
                PaymentStatus.COMPLETED
            } else {
                updatePaymentStatus(paymentSession.paymentId, PaymentStatus.FAILED)
                PaymentStatus.FAILED
            }
            
            val paymentResult = PaymentResult(
                paymentId = paymentSession.paymentId,
                status = status,
                transactionId = if (isSuccess) UUID.randomUUID().toString() else null,
                amount = paymentSession.amount,
                currency = paymentSession.currency,
                processedAt = System.currentTimeMillis(),
                errorMessage = if (!isSuccess) "Payment failed. Please try again." else null
            )
            
            Result.success(paymentResult)
        } catch (e: Exception) {
            updatePaymentStatus(paymentSession.paymentId, PaymentStatus.FAILED)
            Result.failure(e)
        }
    }
    
    override fun getPaymentStatus(paymentId: String): Flow<PaymentStatus> {
        return _paymentStatuses.asStateFlow().map { statuses ->
            statuses[paymentId] ?: PaymentStatus.FAILED
        }
    }
    
    override suspend fun cancelPayment(paymentId: String): Result<Unit> {
        return try {
            updatePaymentStatus(paymentId, PaymentStatus.CANCELLED)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refundPayment(paymentId: String, amount: Double?): Result<Unit> {
        return try {
            updatePaymentStatus(paymentId, PaymentStatus.REFUNDED)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun updatePaymentStatus(paymentId: String, status: PaymentStatus) {
        val currentStatuses = _paymentStatuses.value.toMutableMap()
        currentStatuses[paymentId] = status
        _paymentStatuses.value = currentStatuses
    }
}

// Extension function for Flow mapping
private fun <T, R> Flow<T>.map(transform: suspend (value: T) -> R): Flow<R> {
    return kotlinx.coroutines.flow.flow {
        collect { value ->
            emit(transform(value))
        }
    }
}
