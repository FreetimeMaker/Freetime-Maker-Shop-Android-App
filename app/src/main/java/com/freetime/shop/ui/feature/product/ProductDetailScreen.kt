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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import com.freetime.domain.model.Wallpaper
import com.freetime.domain.viewmodel.CartViewModel
import com.freetime.domain.viewmodel.ProductViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController,
    productViewModel: ProductViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel()
) {
    val wallpaper = productViewModel.getProductById(productId)
    var quantity by remember { mutableIntStateOf(1) }
    
    if (wallpaper == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Wallpaper not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(wallpaper.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Wallpaper Image
            AsyncImage(
                model = wallpaper.imageUrl,
                contentDescription = wallpaper.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Wallpaper Title and Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = wallpaper.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        text = "$${wallpaper.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Badges
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(wallpaper.category.name.replace("_", " ")) }
                    )
                    SuggestionChip(
                        onClick = { },
                        label = { Text(wallpaper.resolution.name.replace("_", " ")) }
                    )
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
                    text = wallpaper.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tags
                if (wallpaper.tags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        wallpaper.tags.forEach { tag ->
                            SuggestionChip(
                                onClick = { },
                                label = { Text("#$tag") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // File Info
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "File Details",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Resolution: ${wallpaper.resolution.name.replace("_", " ")}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        val sizeInMb = wallpaper.fileSize / (1024.0 * 1024.0)
                        Text(
                            text = String.format(Locale.US, "Size: %.2f MB", sizeInMb),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Quantity Selector (though usually 1 for digital goods)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { if (quantity > 1) quantity-- }) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    IconButton(onClick = { quantity++ }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Button(
                    onClick = {
                        cartViewModel.addToCart(wallpaper, quantity)
                        navController.navigate("cart")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(String.format(Locale.US, "Add to Cart - $%.2f", wallpaper.price * quantity))
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = {
                        cartViewModel.addToCart(wallpaper, quantity)
                        navController.navigate("checkout")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Direct Checkout")
                }
            }
        }
    }
}
