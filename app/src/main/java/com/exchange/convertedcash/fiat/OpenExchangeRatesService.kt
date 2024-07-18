package com.exchange.convertedcash.fiat

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeRatesService {
    @GET("latest.json")
    fun getLatestRates(
        @Query("app_id") apiKey: String = "424b379a3e264bf183b6a7bf83b0566f"
    ): Call<OpenExchangeRatesResponse>
}