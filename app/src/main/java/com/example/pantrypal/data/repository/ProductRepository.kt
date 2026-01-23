package com.example.pantrypal.data.repository

import com.example.pantrypal.data.local.ProductDao
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.remote.RetrofitClient

class ProductRepository(private val productDao: ProductDao) {

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

    // --- SYNC MOTORU ---
    suspend fun syncUnsyncedProducts() {
        // 1. Gönderilmemişleri çek
        val unsyncedList = productDao.getUnsyncedProducts()

        if (unsyncedList.isEmpty()) return // Liste boşsa çık

        // 2. Sırayla API'ye gönder
        for (product in unsyncedList) {
            try {
                // DÜZELTME: 'apiService' yerine 'instance' kullanıldı
                val response = RetrofitClient.instance.addProduct(product)

                if (response.isSuccessful) {
                    // 3. Başarılıysa: Yerelde 'status = 0' yap
                    // DÜZELTME: product.id yerine product.uid kullanıldı
                    productDao.updateStatus(product.uid, 0)
                    android.util.Log.d("SYNC", "Sent successfully: ${product.name}")
                } else {
                    android.util.Log.e("SYNC", "Failed to send: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SYNC", "Sync Error: ${e.message}")
            }
        }
    }
}