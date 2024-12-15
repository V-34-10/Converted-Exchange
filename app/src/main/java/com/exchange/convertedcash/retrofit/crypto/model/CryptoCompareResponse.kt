package com.exchange.convertedcash.retrofit.crypto.model

data class CryptoCompareResponse(
    val RAW: Map<String, Map<String, CryptoData>>? = null
)