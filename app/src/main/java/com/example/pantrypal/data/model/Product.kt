package com.example.pantrypal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "products")
data class Product(
    // YEREL ID: Telefonun kendi içinde kullandığı sayısal ID (Otomatik Artar)
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,

    // BULUT ID: MockAPI'nin verdiği String ID.
    // Repository hatasını çözmek için bu alanın adı mutlaka 'id' olmalı.
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("barcode")
    val barcode: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("quantity")
    val quantity: Int,

    // Tarih (Timestamp olarak Long tutulur)
    @SerializedName("expiry_date")
    val expiryDate: Long,

    @SerializedName("image_url")
    val imageUrl: String? = null,

    // Status: 0 = Synced (Gönderildi), 1 = Dirty (Gönderilmeyi Bekliyor)
    val status: Int = 1,

    // Veri İzolasyonu: Ürünün kime ait olduğu
    @SerializedName("owner_id")
    val ownerId: String? = null
)