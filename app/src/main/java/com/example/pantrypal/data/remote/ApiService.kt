package com.example.pantrypal.data.remote

import com.example.pantrypal.data.model.Product
import com.example.pantrypal.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT   // <--- EKLENDİ (Güncelleme için şart)
import retrofit2.http.Path
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

    @GET("product")
    suspend fun getProducts(
        @Query("owner_id") ownerId: String
    ): Response<List<Product>>

    @POST("product")
    suspend fun addProduct(@Body product: Product): Response<Product>

    // Silme Endpoint'i
    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Product>

    // --- YENİ EKLENEN: GÜNCELLEME (EDIT) ENDPOINT'İ ---
    // Bir ürünü değiştirmek için kullanılır. Hem ID'yi hem de yeni veriyi göndeririz.
    @PUT("product/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body product: Product
    ): Response<Product>
}