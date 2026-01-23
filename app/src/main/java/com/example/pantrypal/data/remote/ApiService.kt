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

    @GET("users")
    suspend fun login(
        @Query("username") username: String
    ): Response<List<User>>

    @POST("users")
    suspend fun registerUser(@Body user: User): Response<User>


    // --- PRODUCT ENDPOINTS ---

    // GÜNCELLENDİ: Artık hangi kullanıcının ürünlerini istediğimizi belirtiyoruz.
    // Bu, URL'ye "?owner_id=KullaniciAdi" eklenmesini sağlar.
    @GET("product")
    suspend fun getProducts(
        @Query("owner_id") ownerId: String
    ): Response<List<Product>>

    @POST("product")
    suspend fun addProduct(@Body product: Product): Response<Product>
}