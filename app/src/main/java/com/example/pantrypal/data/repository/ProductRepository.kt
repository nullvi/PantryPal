package com.example.pantrypal.data.repository

import com.example.pantrypal.data.local.ProductDao
import com.example.pantrypal.data.model.Product

// ARTIK CONTEXT YERİNE DAO ALIYOR (Düzeltme Burada)
class ProductRepository(private val productDao: ProductDao) {

    // Tüm ürünleri getir
    suspend fun getAllProducts(): List<Product> {
        // Eğer DAO'daki fonksiyonun adı 'getAll' ise burayı değiştirme.
        // Eğer hata verirse 'getAllProducts' olarak dene.
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