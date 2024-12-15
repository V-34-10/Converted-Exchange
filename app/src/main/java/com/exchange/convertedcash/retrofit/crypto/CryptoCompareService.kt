package com.exchange.convertedcash.retrofit.crypto


import com.exchange.convertedcash.retrofit.crypto.model.CryptoCompareResponse
import com.exchange.convertedcash.retrofit.crypto.model.CryptoListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoCompareService {

    @GET("data/all/coinlist")
    fun getCryptocurrencyList(
        @Query("api_key") apiKey: String = "42b73a713821e42cec6f35201e54504144726686ccd5d56c470b22dba1c2701f"
    ): Call<CryptoListResponse>

    @GET("data/pricemultifull")
    fun getCryptocurrencyPrices(
        @Query("fsyms") fromSymbols: String,
        @Query("tsyms") toSymbols: String = "USD",
        @Query("api_key") apiKey: String = "42b73a713821e42cec6f35201e54504144726686ccd5d56c470b22dba1c2701f"
    ): Call<CryptoCompareResponse>
}