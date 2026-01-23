package com.example.pantrypal.data.repository

import com.example.pantrypal.data.local.ProductDao
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.remote.RetrofitClient

class ProductRepository(private val productDao: ProductDao) {

    // 1. Yerel veritabanından, o kullanıcının verilerini çek
    suspend fun getAllProducts(ownerId: String): List<Product> {
        return productDao.getAll(ownerId)
    }

    // Ürün ekle
    suspend fun insert(product: Product) {
        productDao.insert(product)
    }

    // Ürün sil
    suspend fun delete(product: Product) {
        productDao.delete(product)
    }

    // --- YENİ EKLENEN: BULUTTAN VERİ ÇEKME (FETCH/PULL) ---
    suspend fun refreshProductsFromApi(ownerId: String) {
        try {
            // 1. API'ye sor: "Bu kullanıcının ürünleri var mı?"
            val response = RetrofitClient.instance.getProducts(ownerId)

            if (response.isSuccessful && response.body() != null) {
                val remoteList = response.body()!!

                // 2. Gelen verileri Yerel DB'ye kaydet
                for (remoteProduct in remoteList) {
                    // API'den gelen veri temizdir (Synced = 0)
                    // Ayrıca ownerId'yi garantiye alalım
                    val productToSave = remoteProduct.copy(
                        status = 0, // SYNCED
                        ownerId = ownerId
                    )

                    // Veritabanına yaz (Çakışma varsa üzerine yazar/günceller)
                    productDao.insert(productToSave)
                }
                android.util.Log.d("SYNC", "Fetched ${remoteList.size} items from cloud.")
            }
        } catch (e: Exception) {
            android.util.Log.e("SYNC", "Fetch Error: ${e.message}")
        }
    }

    // --- MEVCUT: BULUTA VERİ GÖNDERME (PUSH) ---
    suspend fun syncUnsyncedProducts() {
        val unsyncedList = productDao.getUnsyncedProducts()

        if (unsyncedList.isEmpty()) return

        for (product in unsyncedList) {
            try {
                val response = RetrofitClient.instance.addProduct(product)

                if (response.isSuccessful) {
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