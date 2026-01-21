package com.example.pantrypal.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pantrypal.data.model.Product

@Dao
interface ProductDao {
    // Repository 'getAll' aradığı için ismini değiştirdik.
    // Flow yerine direkt List döndürüyoruz (suspend ile).
    @Query("SELECT * FROM products ORDER BY expiryDate ASC")
    suspend fun getAll(): List<Product>

    // Repository 'insert' arıyor.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    // Repository 'delete' arıyor.
    @Delete
    suspend fun delete(product: Product)
}