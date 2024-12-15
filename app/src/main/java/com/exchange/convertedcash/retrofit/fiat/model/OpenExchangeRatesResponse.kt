package com.exchange.convertedcash.retrofit.fiat.model

data class OpenExchangeRatesResponse(
    val base: String,
    val rates: Map<String, Double>? = null
)