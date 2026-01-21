package com.example.pantrypal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0, // Sadece telefonun içinde geçerli ID

    @SerializedName("id") // MockAPI'den gelen ID
    val remoteId: String? = null,

    @SerializedName("barcode")
    val barcode: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("expiry_date")
    val expiryDate: String, // "YYYY-MM-DD" formatında tutacağız

    @SerializedName("image_url")
    val imageUrl: String? = null,

    // Bu alan API'de yok, sadece telefonda tutacağız
    // false: Henüz sunucuya gönderilmedi (Dirty)
    // true: Sunucuyla eşitlendi
    val isSynced: Boolean = false
)