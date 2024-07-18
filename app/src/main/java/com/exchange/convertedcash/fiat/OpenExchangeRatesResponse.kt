package com.exchange.convertedcash.fiat

data class OpenExchangeRatesResponse(
    val base: String,
    val rates: Map<String, Double>? = null
)