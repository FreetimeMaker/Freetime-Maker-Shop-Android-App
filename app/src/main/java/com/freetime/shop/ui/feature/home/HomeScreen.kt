package com.freetime.shop.ui.feature.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import com.freetime.domain.model.Product

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = koinViewModel()) {
    val uiState = viewModel.uiState.collectAsState()

    when (uiState.value) {
        is HomeScreenUIEvents.Loading -> {
            CircularProgressIndicator()
        }

        is HomeScreenUIEvents.Success -> {
            val data = (uiState as HomeScreenUIEvents.Success).data
            LazyColumn {
                items(data) { product ->
                    ProductItem(product = product)
                }
            }
        }

        is HomeScreenUIEvents.Error -> {
            Text(text = (uiState as HomeScreenUIEvents.Error).message)
        }
    }
}

@Composable
fun ProductItem(product: Product)