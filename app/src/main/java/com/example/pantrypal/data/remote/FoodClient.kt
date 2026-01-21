package com.example.pantrypal.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FoodClient {

    // Bu adres MockAPI'den farklı, o yüzden yeni dosya açtık
    private const val BASE_URL = "https://world.openfoodfacts.org/api/v0/"

    val api: OpenFoodFactsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsApi::class.java)
    }
}