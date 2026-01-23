package com.example.pantrypal.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pantrypal.data.model.Product

@Dao
interface ProductDao {
    // Listeleme (GÜNCELLENDİ)
    // Sadece parametre olarak gelen 'ownerId'ye ait ürünleri getiriyoruz.
    @Query("SELECT * FROM products WHERE ownerId = :ownerId ORDER BY expiryDate ASC")
    suspend fun getAll(ownerId: String): List<Product>

    // Ekleme
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    // Silme
    @Delete
    suspend fun delete(product: Product)

    // --- SYNC FONKSİYONLARI ---

    // 1. Gönderilmemişleri getir (Status = 1 olanlar)
    @Query("SELECT * FROM products WHERE status = 1")
    suspend fun getUnsyncedProducts(): List<Product>

    // 2. Durum Güncelleme
    @Query("UPDATE products SET status = :newStatus WHERE uid = :productId")
    suspend fun updateStatus(productId: Int, newStatus: Int)
}