package com.example.pantrypal.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.repository.ProductRepository
import kotlinx.coroutines.launch

// 'AndroidViewModel' kullanıyoruz çünkü Repository'ye 'Context' vermemiz lazım
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(application)

    // UI'ın gözlemleyeceği ürün listesi
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // ViewModel başladığında verileri otomatik çek
    init {
        loadProducts()
    }

    fun loadProducts() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Repository'den veriyi al
                val productList = repository.getAllProducts()
                _products.value = productList
            } catch (e: Exception) {
                // Hata olursa (örn: veritabanı boşsa) boş liste dön
                _products.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Ürün silme fonksiyonu
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
            loadProducts() // Listeyi güncelle
        }
    }
}