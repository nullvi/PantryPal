package com.example.pantrypal.data.remote

import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // --- USER ENDPOINTS ---

    // Mevcut Login (Kullanıcı kontrolü)
    @GET("users")
    suspend fun login(
        @Query("username") username: String
    ): Response<List<User>>

    // YENİ EKLENEN: Register (Kullanıcı Kayıt)
    @POST("users")
    suspend fun registerUser(@Body user: User): Response<User>


    // --- PRODUCT ENDPOINTS ---

    // Mevcut Ürün Listeleme
    @GET("product")
    suspend fun getProducts(): Response<List<Product>>

    // Mevcut Ürün Ekleme
    @POST("product")
    suspend fun addProduct(@Body product: Product): Response<Product>
}