package com.example.pantrypal.ui.add_product

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.data.local.AppDatabase
import com.example.pantrypal.data.model.OpenFoodFactsResponse
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.remote.FoodClient
import com.example.pantrypal.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class AddProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository

    // API'den bulunan ismi Activity'e taşımak için canlı veri
    private val _scannedProductName = MutableLiveData<String?>()
    val scannedProductName: LiveData<String?> get() = _scannedProductName

    init {
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
    }

    // Barkodu OpenFoodFacts API'sinde Ara
    fun searchBarcode(barcode: String) {
        if (barcode.isBlank()) return

        FoodClient.api.getProductByBarcode(barcode).enqueue(object : Callback<OpenFoodFactsResponse> {
            override fun onResponse(
                call: Call<OpenFoodFactsResponse>,
                response: Response<OpenFoodFactsResponse>
            ) {
                if (response.isSuccessful) {
                    val productName = response.body()?.product?.productName
                    if (!productName.isNullOrEmpty()) {
                        _scannedProductName.postValue(productName)
                    } else {
                        _scannedProductName.postValue(null)
                    }
                } else {
                    _scannedProductName.postValue(null)
                }
            }

            override fun onFailure(call: Call<OpenFoodFactsResponse>, t: Throwable) {
                _scannedProductName.postValue(null)
            }
        })
    }

    // YENİ ÜRÜN EKLEME (ADD)
    fun addProduct(name: String, quantity: Int, expiryDateStr: String, barcode: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Tarihi Formatla
                val dateLong = parseDateToLong(expiryDateStr)

                // 2. Şu anki kullanıcı adını al (Owner ID)
                val sharedPrefs = getApplication<Application>().getSharedPreferences("PantryPalParams", Context.MODE_PRIVATE)
                val currentOwnerId = sharedPrefs.getString("username", "Guest") ?: "Guest"

                // 3. Product Objesini Oluştur
                val newProduct = Product(
                    barcode = barcode,
                    name = name,
                    quantity = quantity,
                    expiryDate = dateLong,
                    status = 1, // DIRTY (Henüz sunucuya gitmedi)
                    ownerId = currentOwnerId
                )

                // 4. Kaydet
                repository.insert(newProduct)

                launch(Dispatchers.Main) {
                    onResult(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    // --- YENİ EKLENEN: ÜRÜN GÜNCELLEME (EDIT) ---
    fun updateProduct(originalProduct: Product, name: String, quantity: Int, expiryDateStr: String, barcode: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Tarihi Formatla
                val dateLong = parseDateToLong(expiryDateStr)

                // 2. Objeyi Kopyala ve Güncelle
                // copy() metodu ile ID'leri (uid, id, ownerId) koruyoruz, sadece içeriği değiştiriyoruz.
                val updatedProduct = originalProduct.copy(
                    name = name,
                    quantity = quantity,
                    expiryDate = dateLong,
                    barcode = barcode,
                    status = 1 // Değişiklik olduğu için tekrar senkronize edilmeli (Dirty)
                )

                // 3. Repository üzerinden güncelle
                repository.update(updatedProduct)

                launch(Dispatchers.Main) {
                    onResult(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    // Yardımcı Fonksiyon: Tarih Çevirme
    private fun parseDateToLong(dateStr: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}