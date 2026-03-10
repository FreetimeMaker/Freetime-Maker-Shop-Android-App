package com.freetime.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Wallpaper(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "USD",
    val category: WallpaperCategory,
    val resolution: Resolution,
    val imageUrl: String? = null,
    val downloadUrl: String,
    val fileSize: Long,
    val tags: List<String> = emptyList()
)

@Serializable
enum class WallpaperCategory {
    ABSTRACT,
    NATURE,
    CITYSCAPE,
    MINIMALIST,
    TECHNOLOGY,
    ARTISTIC,
    SPACE,
    ANIMALS,
    VINTAGE,
    GEOMETRIC,
    CAT
}

@Serializable
enum class Resolution {
    HD_720P,
    FULL_HD_1080P,
    QHD_1440P,
    UHD_4K,
    UHD_8K,
    MOBILE,
    ULTRAWIDE,
    SQUARE
}

@Serializable
data class CartItem(
    val wallpaper: Wallpaper,
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

// Predefined wallpapers collection
object SampleWallpapers {
    val wallpapers = listOf(
        Wallpaper(
            id = "black_cat",
            title = "Mystic Black Cat",
            description = "A beautiful black cat with piercing eyes against a dark background. Perfect for OLED screens.",
            price = 4.99,
            category = WallpaperCategory.CAT,
            resolution = Resolution.MOBILE,
            imageUrl = "https://images.unsplash.com/photo-1720064921203-22ba396237c2?w=800&auto=format&fit=crop&q=80",
            downloadUrl = "https://example.com/download/black_cat",
            fileSize = 1245821,
            tags = listOf("cat", "black", "minimal", "dark", "oled")
        ),
        Wallpaper(
            id = "neon_city",
            title = "Cyberpunk Cityscape",
            description = "A vibrant futuristic city with neon lights and rainy streets. Cinematic atmosphere.",
            price = 6.99,
            category = WallpaperCategory.CITYSCAPE,
            resolution = Resolution.UHD_4K,
            imageUrl = "https://images.unsplash.com/photo-1605142859862-978be7eba909?w=800&auto=format&fit=crop&q=80",
            downloadUrl = "https://example.com/download/neon_city",
            fileSize = 8547321,
            tags = listOf("neon", "city", "cyberpunk", "future", "4k")
        ),
        Wallpaper(
            id = "mountain_lake",
            title = "Serene Mountain Lake",
            description = "Crystal clear lake reflecting majestic snow-capped mountains during sunset.",
            price = 3.99,
            category = WallpaperCategory.NATURE,
            resolution = Resolution.ULTRAWIDE,
            imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&auto=format&fit=crop&q=80",
            downloadUrl = "https://example.com/download/mountain_lake",
            fileSize = 5241000,
            tags = listOf("nature", "mountain", "lake", "landscape", "serene")
        ),
        Wallpaper(
            id = "abstract_waves",
            title = "Liquid Gold Waves",
            description = "Abstract flowing waves with a metallic golden texture. Modern and elegant.",
            price = 5.49,
            category = WallpaperCategory.ABSTRACT,
            resolution = Resolution.UHD_4K,
            imageUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=800&auto=format&fit=crop&q=80",
            downloadUrl = "https://example.com/download/abstract_waves",
            fileSize = 3412900,
            tags = listOf("abstract", "gold", "liquid", "modern", "premium")
        ),
        Wallpaper(
            id = "deep_space",
            title = "Galactic Nebula",
            description = "Breathtaking view of a colorful nebula deep in space. Stars and cosmic dust.",
            price = 7.99,
            category = WallpaperCategory.SPACE,
            resolution = Resolution.UHD_8K,
            imageUrl = "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=800&auto=format&fit=crop&q=80",
            downloadUrl = "https://example.com/download/deep_space",
            fileSize = 12541000,
            tags = listOf("space", "nebula", "galaxy", "stars", "8k")
        ),
        Wallpaper(
            id = "minimal_desert",
            title = "Minimalist Desert",
            description = "Simple and clean illustration of sand dunes under a soft pastel sky.",
            price = 2.99,
            category = WallpaperCategory.MINIMALIST,
            resolution = Resolution.MOBILE,
            imageUrl = "https://images.unsplash.com/photo-1509316785289-025f5b846b35?w=800&auto=format&fit=crop&q=80",
            downloadUrl = "https://example.com/download/minimal_desert",
            fileSize = 850000,
            tags = listOf("minimal", "desert", "pastel", "clean", "mobile")
        )
    )
}
