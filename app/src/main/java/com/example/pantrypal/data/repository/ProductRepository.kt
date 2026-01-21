package com.example.pantrypal.data.repository

import android.content.Context
import com.example.pantrypal.data.local.AppDatabase
import com.example.pantrypal.data.model.Product

class ProductRepository(context: Context) {
    // Veritabanı örneğini alıyoruz
    private val db = AppDatabase.getDatabase(context)
    private val productDao = db.productDao()

    // Tüm ürünleri getir
    suspend fun getAllProducts(): List<Product> {
        return productDao.getAll()
    }

    // Ürün ekle
    suspend fun insert(product: Product) {
        productDao.insert(product)
    }

    // Ürün sil
    suspend fun delete(product: Product) {
        productDao.delete(product)
    }
}