package com.exchange.convertedcash.crypto

data class CryptoListResponse(
    val response: String,
    val message: String,
    val data: Map<String, CryptoInfo>
)

data class CryptoInfo(
    val id: String,
    val name: String,
    val fullName: String
)