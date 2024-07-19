package com.exchange.convertedcash.crypto

data class CryptoCompareResponse(
    val RAW: Map<String, Map<String, CryptoData>>? = null
)