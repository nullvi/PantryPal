package com.example.pantrypal.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.data.model.User
import com.example.pantrypal.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    // Repository'yi manuel olarak initialize ediyoruz (DI kullanmadığımız için)
    private val repository = AuthRepository()

    // UI'ın gözlemleyeceği sonuç değişkeni (Başarılı mı? Hata mı?)
    private val _registerResult = MutableLiveData<Result<User>>()
    val registerResult: LiveData<Result<User>> = _registerResult

    fun register(username: String, pass: String) {
        viewModelScope.launch {
            try {
                // User objesini oluştur
                val newUser = User(id = "0", username = username, password = pass)

                // Repository'e gönder
                val response = repository.register(newUser)

                if (response.isSuccessful && response.body() != null) {
                    _registerResult.value = Result.success(response.body()!!)
                } else {
                    _registerResult.value = Result.failure(Exception("Registration failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            }
        }
    }
}