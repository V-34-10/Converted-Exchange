package com.exchange.convertedcash.crypto

data class CryptoListResponse(
    val Response: String,
    val Message: String,
    val Data: Map<String, CryptoInfo>
)

data class CryptoInfo(
    val Id: String,
    val Name: String,
    val FullName: String
)