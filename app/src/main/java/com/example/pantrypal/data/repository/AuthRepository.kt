package com.example.pantrypal.data.repository

import com.example.pantrypal.data.model.User
import com.example.pantrypal.data.remote.RetrofitClient
import retrofit2.Response

class AuthRepository {
    // Retrofit servisini çağırır
    private val api = RetrofitClient.instance

    // MEVCUT: Kullanıcı adı ile eşleşen kullanıcıları getirir (Login mantığı)
    suspend fun login(username: String): Response<List<User>> {
        return api.login(username)
    }

    // YENİ EKLENEN: Yeni kullanıcı oluşturur (Register mantığı)
    // MockAPI /users endpoint'ine POST isteği atarak kullanıcıyı kaydeder.
    suspend fun register(user: User): Response<User> {
        return api.registerUser(user)
    }
}