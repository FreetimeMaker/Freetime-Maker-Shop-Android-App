package com.freetime.shop.ui.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.freetime.domain.model.WallpaperCategory
import com.freetime.domain.model.Resolution
import com.freetime.domain.viewmodel.ProductViewModel
import com.freetime.domain.viewmodel.ProductUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ProductViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedResolution by viewModel.selectedPlatform.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Freetime Wallpaper Shop", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Category Filter
            CategoryFilter(
                selectedCategory = selectedCategory,
                onCategorySelected = viewModel::filterByCategory
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Resolution Filter
            ResolutionFilter(
                selectedResolution = selectedResolution,
                onResolutionSelected = viewModel::filterByPlatform
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Clear Filters Button
            if (selectedCategory != null || selectedResolution != null) {
                OutlinedButton(
                    onClick = { viewModel.clearFilters() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Filters")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Content
            when (uiState) {
                is ProductUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                
                is ProductUIState.Success -> {
                    if (filteredProducts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No wallpapers found matching your filters")
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredProducts) { wallpaper ->
                                WallpaperCard(
                                    wallpaper = wallpaper,
                                    onProductClick = { 
                                        navController.navigate("product/${wallpaper.id}")
                                    }
                                )
                            }
                        }
                    }
                }
                
                is ProductUIState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (uiState as ProductUIState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.refreshProducts() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilter(
    selectedCategory: WallpaperCategory?,
    onCategorySelected: (WallpaperCategory?) -> Unit
) {
    val categories = listOf(
        null to "All",
        WallpaperCategory.ABSTRACT to "Abstract",
        WallpaperCategory.NATURE to "Nature",
        WallpaperCategory.CITYSCAPE to "Cityscape",
        WallpaperCategory.CAT to "Cats",
        WallpaperCategory.SPACE to "Space",
        WallpaperCategory.MINIMALIST to "Minimalist"
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories.size) { index ->
            val (category, name) = categories[index]
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(name) }
            )
        }
    }
}

@Composable
fun ResolutionFilter(
    selectedResolution: Resolution?,
    onResolutionSelected: (Resolution?) -> Unit
) {
    val resolutions = listOf(
        null to "Any Res",
        Resolution.MOBILE to "Mobile",
        Resolution.FULL_HD_1080P to "1080p",
        Resolution.UHD_4K to "4K",
        Resolution.ULTRAWIDE to "Ultrawide"
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(resolutions.size) { index ->
            val (res, name) = resolutions[index]
            FilterChip(
                selected = selectedResolution == res,
                onClick = { onResolutionSelected(res) },
                label = { Text(name) }
            )
        }
    }
}

@Composable
fun WallpaperCard(
    wallpaper: Wallpaper,
    onProductClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        onClick = onProductClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = wallpaper.imageUrl,
                    contentDescription = wallpaper.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = wallpaper.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${wallpaper.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = wallpaper.resolution.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
