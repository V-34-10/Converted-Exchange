package com.exchange.convertedcash.retrofit.fiat

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientRates {
    private const val BASE_URL = "https://openexchangerates.org/api/"

    val apiService: OpenExchangeRatesService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenExchangeRatesService::class.java)
    }
}