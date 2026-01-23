package com.example.pantrypal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,

    @SerializedName("id")
    val remoteId: String? = null,

    @SerializedName("barcode")
    val barcode: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("quantity")
    val quantity: Int,

    // String DEĞİL Long olmalı!
    @SerializedName("expiry_date")
    val expiryDate: Long,

    @SerializedName("image_url")
    val imageUrl: String? = null,

    // Status alanı (1 = Dirty/Gönderilmeyi Bekliyor)
    val status: Int = 1,

    // YENİ EKLENEN: Veri izolasyonu için kullanıcı kimliği
    // MockAPI'de "owner_id" sütununa karşılık gelecek.
    @SerializedName("owner_id")
    val ownerId: String
)