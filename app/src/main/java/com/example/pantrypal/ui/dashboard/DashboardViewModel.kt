package com.example.pantrypal.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.data.local.AppDatabase
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.repository.ProductRepository
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    val products = MutableLiveData<List<Product>>()
    val isLoading = MutableLiveData<Boolean>()

    init {
        // HATA BURADAYDI: Repository artık Context değil, DAO istiyor.
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)

        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Veritabanından verileri çek
                val productList = repository.getAllProducts()
                products.value = productList
            } catch (e: Exception) {
                // Hata olursa boş liste gösterilebilir
                products.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
            loadProducts() // Silince listeyi yenile
        }
    }
}