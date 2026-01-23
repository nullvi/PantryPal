package com.example.pantrypal.data.remote

import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // Login (Users kaynağına gidiyor - Bu doğru)
    @GET("users")
    suspend fun login(
        @Query("username") username: String
    ): Response<List<User>>

    // DÜZELTME: 'products' yerine 'product' (Senin MockAPI ismin)
    @GET("product")
    suspend fun getProducts(): Response<List<Product>>

    // DÜZELTME: 'products' yerine 'product' (Senin MockAPI ismin)
    @POST("product")
    suspend fun addProduct(@Body product: Product): Response<Product>
}