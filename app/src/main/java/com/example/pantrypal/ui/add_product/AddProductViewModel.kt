package com.example.pantrypal.ui.add_product

import android.app.Application
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

    // 1. YENİ: API'den bulunan ismi Activity'e taşımak için canlı veri kutusu
    private val _scannedProductName = MutableLiveData<String?>()
    val scannedProductName: LiveData<String?> get() = _scannedProductName

    init {
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
    }

    // 2. YENİ: Barkodu OpenFoodFacts API'sinde Ara
    fun searchBarcode(barcode: String) {
        if (barcode.isBlank()) return

        // Arka planda internet isteği atıyoruz
        FoodClient.api.getProductByBarcode(barcode).enqueue(object : Callback<OpenFoodFactsResponse> {
            override fun onResponse(
                call: Call<OpenFoodFactsResponse>,
                response: Response<OpenFoodFactsResponse>
            ) {
                if (response.isSuccessful) {
                    // JSON içinden ürün ismini çek
                    val productName = response.body()?.product?.productName

                    if (!productName.isNullOrEmpty()) {
                        // Ürün bulundu! Activity dinlesin diye post et
                        _scannedProductName.postValue(productName)
                    } else {
                        // Ürün bulundu ama ismi yok
                        _scannedProductName.postValue(null)
                    }
                } else {
                    // API hatası (404 vs.)
                    _scannedProductName.postValue(null)
                }
            }

            override fun onFailure(call: Call<OpenFoodFactsResponse>, t: Throwable) {
                // İnternet yoksa veya bağlantı hatası varsa
                _scannedProductName.postValue(null)
            }
        })
    }

    // 3. MEVCUT: Veritabanına Kaydetme (Database Logic)
    fun addProduct(name: String, quantity: Int, expiryDateStr: String, barcode: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dateLong = try {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    sdf.parse(expiryDateStr)?.time ?: System.currentTimeMillis()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }

                val newProduct = Product(
                    barcode = barcode,
                    name = name,
                    quantity = quantity,
                    expiryDate = dateLong,
                    status = 1 // DIRTY
                )

                repository.insert(newProduct)

                launch(Dispatchers.Main) {
                    onResult(true)
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }
}