package com.freetime.shop.ui.feature.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import com.freetime.domain.model.Product
import com.freetime.domain.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController,
    cartViewModel: CartViewModel = koinViewModel()
) {
    // This would typically come from a ProductViewModel
    // For now, we'll use a sample product or get it from repository
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var quantity by remember { mutableStateOf(1) }
    
    LaunchedEffect(productId) {
        // In a real app, you'd get this from ProductViewModel
        // For demo purposes, we'll create a sample product
        isLoading = false
        product = Product(
            id = productId,
            title = "Sample Product",
            description = "This is a sample product description. It would contain details about the product features, specifications, and other relevant information.",
            price = 25.0,
            category = com.freetime.domain.model.ProductCategory.GAMES,
            platform = com.freetime.domain.model.Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/",
            features = listOf("Feature 1", "Feature 2", "Feature 3")
        )
    }
    
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        product == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found")
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                TopAppBar(
                    title = { Text(product!!.title) },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { navController.navigate("cart") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    }
                )
                
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Product Image Placeholder
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Product Image",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Product Title and Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = product!!.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "$${product!!.price}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Category and Platform badges
                        Column {
                            SuggestionChip(
                                onClick = { },
                                label = { Text(product!!.category.name.replace("_", " ")) }
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            SuggestionChip(
                                onClick = { },
                                label = { Text(product!!.platform.name) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Description
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = product!!.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Features
                    if (product!!.features.isNotEmpty()) {
                        Text(
                            text = "Features",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        product!!.features.forEach { feature ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "• $feature",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Quantity Selector
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease quantity"
                            )
                        }
                        
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        IconButton(
                            onClick = { quantity++ }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase quantity"
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Add to Cart Button
                    Button(
                        onClick = {
                            product?.let { 
                                cartViewModel.addToCart(it, quantity)
                                navController.navigate("cart")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add to Cart - $${product!!.price * quantity}")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Buy Now Button
                    OutlinedButton(
                        onClick = {
                            product?.let { 
                                cartViewModel.addToCart(it, quantity)
                                navController.navigate("checkout")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Buy Now")
                    }
                }
            }
        }
    }
}
