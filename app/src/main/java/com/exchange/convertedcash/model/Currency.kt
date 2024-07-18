package com.exchange.convertedcash.model

data class Currency(
    val code: String,
    val name: String? = null,
    val rate: Double? = null
)