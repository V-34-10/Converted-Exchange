package com.exchange.convertedcash.retrofit.crypto.model

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