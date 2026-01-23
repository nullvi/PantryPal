package com.example.pantrypal.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.data.local.AppDatabase // Senin dosyanın adı bu
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    val products = MutableLiveData<List<Product>>()
    val isLoading = MutableLiveData<Boolean>()

    init {
        // DÜZELTİLDİ: Senin dosya ismin olan 'AppDatabase' kullanıldı.
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
    }

    // Ürünleri Listele
    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) { // Arka planda çalış
            isLoading.postValue(true)
            try {
                val productList = repository.getAllProducts()
                products.postValue(productList)
            } catch (e: Exception) {
                products.postValue(emptyList())
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

    // --- SYNC MOTORUNU ÇALIŞTIR (Yeni) ---
    fun syncPendingData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Repository'deki "Postacıyı" çalıştır
            repository.syncUnsyncedProducts()

            // İşlem bitince listeyi tazelemek iyi olur
            loadProducts()
        }
    }
}