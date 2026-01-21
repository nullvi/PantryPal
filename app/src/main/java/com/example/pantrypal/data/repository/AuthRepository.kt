package com.example.pantrypal.data.repository

import com.example.pantrypal.data.model.User
import com.example.pantrypal.data.remote.RetrofitClient
import retrofit2.Response

class AuthRepository {
    // Retrofit servisini çağırır
    private val api = RetrofitClient.instance

    // Kullanıcı adı ile eşleşen kullanıcıları getirir (MockAPI mantığı)
    suspend fun login(username: String): Response<List<User>> {
        // Not: MockAPI'de filtreleme genellikle '?username=X' ile yapılır.
        // ApiService içindeki metodun @GET("users") fun login(@Query("username") username: String) şeklinde olmalı.
        return api.login(username)
    }
}