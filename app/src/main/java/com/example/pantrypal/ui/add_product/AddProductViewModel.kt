package com.example.pantrypal.ui.add_product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.repository.ProductRepository
import kotlinx.coroutines.launch

class AddProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(application)

    fun addProduct(name: String, quantity: Int, expiryDate: String, onResult: (Boolean) -> Unit) {
        if (name.isEmpty() || quantity < 1 || expiryDate.isEmpty()) {
            onResult(false) // Validasyon hatası
            return
        }

        viewModelScope.launch {
            val newProduct = Product(
                barcode = "", // Manuel eklemede barkod boş olabilir veya "MANUAL" yazılabilir
                name = name,
                quantity = quantity,
                expiryDate = expiryDate,
                imageUrl = "",
                isSynced = false // Offline-first mantığı: Henüz sunucuya gitmedi
            )
            repository.insert(newProduct)
            onResult(true) // Başarılı
        }
    }
}