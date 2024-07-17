package com.exchange.convertedcash.model

data class OpenExchangeRatesResponse(
    val base: String? = null,
    val rates: Map<String, Double>? = null
)