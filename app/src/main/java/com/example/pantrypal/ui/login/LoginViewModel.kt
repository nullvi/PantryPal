package com.example.pantrypal.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    // UI'ın gözlemleyeceği durumlar (State)
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun loginUser(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Username and password cannot be empty"
            return
        }

        _isLoading.value = true

        // Coroutine başlatıyoruz (Main thread'i kilitlememek için)
        viewModelScope.launch {
            try {
                // Repository'ye istek atıyoruz
                val response = repository.login(username)

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    // Kullanıcı bulundu, şimdi şifre kontrolü (Basit String eşleşmesi)
                    val user = response.body()!![0]
                    if (user.password == password) {
                        _loginResult.value = true // Başarılı
                    } else {
                        _errorMessage.value = "Invalid password"
                        _loginResult.value = false
                    }
                } else {
                    _errorMessage.value = "User not found"
                    _loginResult.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Connection error: ${e.message}"
                _loginResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}