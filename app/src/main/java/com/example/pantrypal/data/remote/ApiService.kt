package com.example.pantrypal.data.remote

import com.example.pantrypal.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("users")
    suspend fun login(
        @Query("username") username: String
    ): Response<List<User>>
}