package com.example.pantrypal.data.model

import com.google.gson.annotations.SerializedName

// API'den gelen ana cevap kutusu
data class OpenFoodFactsResponse(
    @SerializedName("product")
    val product: ProductDetails? = null,

    @SerializedName("status")
    val status: Int = 0
)

// Cevabın içindeki ürün detayları
data class ProductDetails(
    @SerializedName("product_name")
    val productName: String? = null,

    @SerializedName("image_url")
    val imageUrl: String? = null
)