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

    // Ürünleri Listele (GÜNCELLENDİ)
    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                // 1. ÖNCE: Şu anki kullanıcı adını hafızadan (SharedPreferences) öğren
                val sharedPrefs = getApplication<Application>().getSharedPreferences("PantryPalParams", Context.MODE_PRIVATE)
                // Eğer username yoksa boş string döner
                val currentUser = sharedPrefs.getString("username", "") ?: ""

                // 2. SONRA: Repository'e "Sadece bu kullanıcının verilerini ver" de
                if (currentUser.isNotEmpty()) {
                    val productList = repository.getAllProducts(currentUser)
                    products.postValue(productList)
                } else {
                    // Kullanıcı giriş yapmamışsa boş liste göster
                    products.postValue(emptyList())
                }

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

    // Sync Motoru
    fun syncPendingData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncUnsyncedProducts()
            loadProducts()
        }
    }
}