package com.freetime.shop.ui.feature.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.freetime.domain.viewmodel.CartViewModel
import com.freetime.domain.viewmodel.CheckoutViewModel
import com.freetime.domain.viewmodel.CheckoutUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartViewModel: CartViewModel = koinViewModel(),
    checkoutViewModel: CheckoutViewModel = koinViewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartTotal by cartViewModel.cartTotal.collectAsState()
    val checkoutUIState by checkoutViewModel.uiState.collectAsState()
    val email by checkoutViewModel.email.collectAsState()
    val isProcessing by checkoutViewModel.isProcessing.collectAsState()
    
    var emailInput by remember { mutableStateOf("") }
    
    LaunchedEffect(email) {
        emailInput = email
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Checkout") },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Order Summary
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Order Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    cartItems.forEach { cartItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = cartItem.wallpaper.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Qty: ${cartItem.quantity}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$${cartItem.wallpaper.price * cartItem.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HorizontalDivider()
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${cartTotal}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Customer Information
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Customer Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { 
                            emailInput = it
                            checkoutViewModel.updateEmail(it)
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("Enter your email address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isProcessing
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Your download links will be sent to this email after purchase confirmation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Payment Information
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Payment Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Secure Payment",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Secure payment processed via FreetimeSDK",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "• Support for multiple Cryptocurrencies\n• Direct Wallet integration\n• Fast and secure",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action Buttons
            Column {
                Button(
                    onClick = {
                        val encodedEmail = URLEncoder.encode(
                            emailInput,
                            StandardCharsets.UTF_8.toString()
                        )
                        // Redirect to the unified PaymentScreen
                        navController.navigate("payment/$cartTotal/BTC/$encodedEmail")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing && emailInput.isNotBlank()
                ) {
                    Text("Pay with Crypto Wallet - $${cartTotal}")
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing
                ) {
                    Text("Back to Cart")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Handle checkout states if using checkoutViewModel.performCheckout()
    // However, we are navigating to PaymentScreen directly for better FreetimeSDK integration
    LaunchedEffect(checkoutUIState) {
        if (checkoutUIState is CheckoutUIState.Success) {
            navController.navigate("order_success") {
                popUpTo("home") { inclusive = false }
            }
        }
    }
}
