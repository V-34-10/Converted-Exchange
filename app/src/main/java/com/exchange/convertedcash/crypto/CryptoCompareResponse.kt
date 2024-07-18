package com.exchange.convertedcash.crypto

data class CryptoCompareResponse(
    val raw: Map<String, Map<String, CryptoData>>? = null
)