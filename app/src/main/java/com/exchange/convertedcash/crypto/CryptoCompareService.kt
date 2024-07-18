package com.exchange.convertedcash.crypto


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoCompareService {

    @GET("data/coinlist")
    fun getCryptocurrencyList(
        @Query("api_key") apiKey: String = "ff02f8af65b42491727a1961cda2822abbe6d6b4a9ea5cebdd256576a818b7e2"
    ): Call<CryptoListResponse>

    @GET("data/pricemultifull")
    fun getCryptocurrencyPrices(
        @Query("fsyms") fromSymbols: String,
        @Query("tsyms") toSymbols: String = "USD",
        @Query("api_key") apiKey: String = "ff02f8af65b42491727a1961cda2822abbe6d6b4a9ea5cebdd256576a818b7e2"
    ): Call<CryptoCompareResponse>
}