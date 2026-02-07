package com.freetime.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "USD",
    val category: ProductCategory,
    val platform: Platform,
    val imageUrl: String? = null,
    val purchaseUrl: String,
    val stock: Int = Int.MAX_VALUE,
    val features: List<String> = emptyList()
)

@Serializable
enum class ProductCategory {
    GAMES,
    CLICKER_GAMES,
    BUNDLES,
    UTILITIES,
    TOKENS,
    DONATIONS
}

@Serializable
enum class Platform {
    ANDROID,
    WINDOWS,
    LINUX,
    ALL
}

@Serializable
data class CartItem(
    val product: Product,
    val quantity: Int
)

@Serializable
data class Order(
    val id: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val currency: String,
    val status: OrderStatus,
    val createdAt: Long,
    val customerEmail: String,
    val paymentId: String? = null
)

enum class OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

// Predefined products based on the webshop
object SampleProducts {
    val products = listOf(
        Product(
            id = "platformer_android",
            title = "2D Platformer",
            description = "Classic 2D platformer game for Android",
            price = 20.0,
            category = ProductCategory.GAMES,
            platform = Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.platformer.android.html",
            features = listOf("Classic gameplay", "Multiple levels", "Android optimized")
        ),
        Product(
            id = "plc_android",
            title = "Programming Language Clicker",
            description = "Learn programming languages while clicking",
            price = 22.0,
            category = ProductCategory.CLICKER_GAMES,
            platform = Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.plc.android.html",
            features = listOf("Educational", "Addictive gameplay", "Learn programming")
        ),
        Product(
            id = "plc_windows",
            title = "Programming Language Clicker",
            description = "Learn programming languages while clicking",
            price = 22.0,
            category = ProductCategory.CLICKER_GAMES,
            platform = Platform.WINDOWS,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.plc.windows.html",
            features = listOf("Educational", "Addictive gameplay", "Learn programming")
        ),
        Product(
            id = "plc2_android",
            title = "Programming Language Clicker 2.0",
            description = "Enhanced version with more features",
            price = 25.0,
            category = ProductCategory.CLICKER_GAMES,
            platform = Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.plc2.android.html",
            features = listOf("Enhanced graphics", "More languages", "Improved gameplay")
        ),
        Product(
            id = "plcb_android",
            title = "PLC Bundle",
            description = "Complete Programming Language Clicker Bundle",
            price = 30.0,
            category = ProductCategory.BUNDLES,
            platform = Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.plcb.android.html",
            features = listOf("All PLC versions", "Best value", "Complete collection")
        ),
        Product(
            id = "cat_clicker_android",
            title = "Cat Clicker",
            description = "Adorable cat clicking game",
            price = 25.0,
            category = ProductCategory.CLICKER_GAMES,
            platform = Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.cat.android.html",
            features = listOf("Cute cats", "Relaxing gameplay", "Collect different cats")
        ),
        Product(
            id = "os_clicker_android",
            title = "OS Clicker",
            description = "Operating System themed clicker",
            price = 25.0,
            category = ProductCategory.CLICKER_GAMES,
            platform = Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.os.android.html",
            features = listOf("OS themes", "Educational", "Tech focused")
        ),
        Product(
            id = "crypto_clicker_android",
            title = "Crypto Clicker",
            description = "Cryptocurrency themed clicking game",
            price = 25.0,
            category = ProductCategory.CLICKER_GAMES,
            platform = Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.cc.android.html",
            features = listOf("Crypto themes", "Learn about crypto", "Trading simulation")
        ),
        Product(
            id = "clicker_bundle_android",
            title = "Clicker Bundle",
            description = "All clicker games in one bundle",
            price = 55.0,
            category = ProductCategory.BUNDLES,
            platform = Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.clicker.bundle.android.html",
            features = listOf("All clicker games", "Huge savings", "Complete collection")
        ),
        Product(
            id = "geoweather_android",
            title = "GeoWeather",
            description = "Weather and geography app",
            price = 15.0,
            category = ProductCategory.UTILITIES,
            platform = Platform.ANDROID,
            purchaseUrl = "https://freetimemaker.github.io/Freetime-Maker-Shop/buy.gw.android.html",
            features = listOf("Weather data", "Geographic info", "Location based")
        )
    )
}
