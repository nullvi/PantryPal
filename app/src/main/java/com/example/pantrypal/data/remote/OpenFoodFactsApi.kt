package com.example.pantrypal.data.remote

import com.example.pantrypal.data.model.OpenFoodFactsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {
    // Barkodu alıp ürün detayını getiren fonksiyon
    @GET("product/{barcode}.json")
    fun getProductByBarcode(@Path("barcode") barcode: String): Call<OpenFoodFactsResponse>
}