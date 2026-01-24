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

    // --- YENİ EKLENEN: GÜNCELLEME (EDIT) FONKSİYONU ---
    suspend fun update(product: Product) {
        // 1. Önce Local DB'de güncelle
        // Güvenlik önlemi: Status'u 1 (Dirty) yapıyoruz.
        // Eğer internet yoksa, Sync motoru bunu sonra yakalar.
        val productToUpdate = product.copy(status = 1)
        productDao.update(productToUpdate)

        // 2. Eğer ürün Bulutta varsa (ID'si varsa), orayı da güncellemeye çalış
        if (!product.id.isNullOrEmpty()) {
            try {
                val response = RetrofitClient.instance.updateProduct(product.id, product)

                if (response.isSuccessful) {
                    // Başarılı! Local DB'de status'u 0 (Synced) yap.
                    productDao.updateStatus(product.uid, 0)
                    android.util.Log.d("SYNC", "Updated on Cloud: ${product.name}")
                } else {
                    android.util.Log.e("SYNC", "Cloud update failed: ${response.code()}")
                }
            } catch (e: Exception) {
                // İnternet yoksa sorun yok, Local'de zaten status=1 yaptık.
                android.util.Log.e("SYNC", "Cloud Update Error: ${e.message}")
            }
        }
    }

    // --- SİLME FONKSİYONU ---
    suspend fun delete(product: Product) {
        // ADIM 1: Önce Yerel Veritabanından (Room) SİL (Garanti olsun diye UID ile)
        productDao.deleteByUid(product.uid)

        // ADIM 2: Cloud'dan sil
        if (!product.id.isNullOrEmpty()) {
            try {
                val response = RetrofitClient.instance.deleteProduct(product.id)

                if (response.isSuccessful) {
                    android.util.Log.d("SYNC", "Deleted from Cloud: ${product.name}")
                } else {
                    android.util.Log.e("SYNC", "Failed to delete from Cloud: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SYNC", "Cloud Delete Error: ${e.message}")
            }
        }
    }

    // --- BULUTTAN VERİ ÇEKME (FETCH/PULL) ---
    suspend fun refreshProductsFromApi(ownerId: String) {
        try {
            val response = RetrofitClient.instance.getProducts(ownerId)

            if (response.isSuccessful && response.body() != null) {
                val remoteList = response.body()!!

                for (remoteProduct in remoteList) {
                    val productToSave = remoteProduct.copy(
                        status = 0, // SYNCED
                        ownerId = ownerId
                    )
                    productDao.insert(productToSave)
                }
                android.util.Log.d("SYNC", "Fetched ${remoteList.size} items from cloud.")
            }
        } catch (e: Exception) {
            android.util.Log.e("SYNC", "Fetch Error: ${e.message}")
        }
    }

    // --- BULUTA VERİ GÖNDERME (PUSH) - GÜNCELLENDİ ---
    // Artık hem yeni eklenenleri (POST) hem de düzenlenenleri (PUT) ayırt edip gönderir.
    suspend fun syncUnsyncedProducts() {
        val unsyncedList = productDao.getUnsyncedProducts()

        if (unsyncedList.isEmpty()) return

        for (product in unsyncedList) {
            try {
                // KARAR ANI: Bu ürün yeni mi yoksa düzenlenmiş mi?
                // ID'si yoksa -> YENİ (POST)
                // ID'si varsa -> DÜZENLENMİŞ (PUT)

                val response = if (product.id.isNullOrEmpty()) {
                    RetrofitClient.instance.addProduct(product)
                } else {
                    RetrofitClient.instance.updateProduct(product.id, product)
                }

                if (response.isSuccessful) {
                    productDao.updateStatus(product.uid, 0)
                    android.util.Log.d("SYNC", "Sync Success (Create/Edit): ${product.name}")
                } else {
                    android.util.Log.e("SYNC", "Sync Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SYNC", "Sync Error: ${e.message}")
            }
        }
    }
}