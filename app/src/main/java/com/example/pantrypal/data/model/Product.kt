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

    // Status alanı EKLENMELİ!
    val status: Int = 1
)