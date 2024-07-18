package com.exchange.convertedcash.model

data class Cryptocurrency(
    val id: String? = null,
    val symbol: String,
    val name: String,
    val usdPrice: Double? = null
)