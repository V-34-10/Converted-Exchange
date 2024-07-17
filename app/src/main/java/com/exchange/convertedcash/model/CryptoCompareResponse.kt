package com.exchange.convertedcash.model

data class CryptoCompareResponse(
    val USD: Double? = null,
    val EUR: Double? = null,
    val UAH: Double? = null,
    val BTC: Double? = null,
    val ETH: Double? = null,
    val BNB: Double? = null
) {
    fun getCurrency(currency: String): Double? {
        return when (currency.uppercase()) {
            "USD" -> USD
            "EUR" -> EUR
            "UAH" -> UAH
            "BTC" -> BTC
            "ETH" -> ETH
            "BNB" -> BNB
            else -> null
        }
    }
}