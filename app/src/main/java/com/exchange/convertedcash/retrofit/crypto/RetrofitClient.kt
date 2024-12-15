package com.exchange.convertedcash.retrofit.crypto

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://min-api.cryptocompare.com/"

    val apiService: CryptoCompareService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CryptoCompareService::class.java)
    }
}