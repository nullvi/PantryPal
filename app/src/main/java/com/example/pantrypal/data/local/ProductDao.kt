package com.example.pantrypal.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pantrypal.data.model.Product

@Dao
interface ProductDao {
    // Listeleme
    @Query("SELECT * FROM products ORDER BY expiryDate ASC")
    suspend fun getAll(): List<Product>

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
    // DÜZELTME: 'id' yerine senin modelindeki 'uid' ismini kullandık.
    @Query("UPDATE products SET status = :newStatus WHERE uid = :productId")
    suspend fun updateStatus(productId: Int, newStatus: Int)
}