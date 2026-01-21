package com.example.pantrypal.data.remote

import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // 1. Login Kontrolü
    // MockAPI filtreleme özelliği: /users?username=admin&password=123
    @GET("users")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<List<User>>

    // 2. Ürünleri Getir (Sync için)
    @GET("products")
    fun getProducts(): Call<List<Product>>

    // 3. Yeni Ürün Gönder (Offline'dan Online'a)
    @POST("products")
    fun addProduct(@Body product: Product): Call<Product>
}