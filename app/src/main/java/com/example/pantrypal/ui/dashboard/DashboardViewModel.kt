package com.example.pantrypal.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.data.local.AppDatabase
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    val products = MutableLiveData<List<Product>>()
    val isLoading = MutableLiveData<Boolean>()

    init {
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
    }

    // Ürünleri Listele (GÜNCELLENDİ: Hem Yerel Hem Bulut)
    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val sharedPrefs = getApplication<Application>().getSharedPreferences("PantryPalParams", Context.MODE_PRIVATE)
                val currentUser = sharedPrefs.getString("username", "") ?: ""

                if (currentUser.isNotEmpty()) {
                    // 1. ADIM: Hız için hemen YEREL veriyi göster
                    val localList = repository.getAllProducts(currentUser)
                    products.postValue(localList)

                    // 2. ADIM: Arka planda BULUTTAN yeni veri çek (Fetch/Pull)
                    // Bu işlem veritabanını günceller
                    repository.refreshProductsFromApi(currentUser)

                    // 3. ADIM: Güncellenmiş veritabanını tekrar oku ve ekrana bas
                    val updatedList = repository.getAllProducts(currentUser)
                    products.postValue(updatedList)

                } else {
                    products.postValue(emptyList())
                }

            } catch (e: Exception) {
                // Hata olsa bile en azından yerel veriler ekranda kalır, listeyi boşaltmıyoruz.
                e.printStackTrace()
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    // Ürün Sil
    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(product)
            loadProducts() // Listeyi güncelle
        }
    }

    // Sync Motoru (Bekleyenleri Gönder)
    fun syncPendingData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncUnsyncedProducts()
            // Gönderme bitince bir de yenilerini çekelim ki tam senkron olsun
            loadProducts()
        }
    }
}