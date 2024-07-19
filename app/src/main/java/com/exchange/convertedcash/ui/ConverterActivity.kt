package com.exchange.convertedcash.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.exchange.convertedcash.R
import com.exchange.convertedcash.crypto.RetrofitClient
import com.exchange.convertedcash.databinding.ActivityConverterBinding
import com.exchange.convertedcash.fiat.OpenExchangeRatesResponse
import com.exchange.convertedcash.fiat.RetrofitClientRates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConverterActivity : AppCompatActivity() {

    private val binding by lazy { ActivityConverterBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        fetchAndPopulateCurrencies()
        controlButton()
    }

    private fun controlButton() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.scale_btn)
        binding.convertButton.setOnClickListener {
            it.startAnimation(animation)
            convertCurrency()
        }
        binding.btnBack.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@ConverterActivity, MenuCategoryActivity::class.java))
            finish()
        }
        binding.fromCurrencySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCurrency = parent.getItemAtPosition(position).toString()
                    val resourceImage = changeImageSelectedSpinnersItem(selectedCurrency)
                    binding.fromCurrencyLogo.setBackgroundResource(resourceImage)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        binding.toCurrencySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCurrency = parent.getItemAtPosition(position).toString()
                    val resourceImage = changeImageSelectedSpinnersItem(selectedCurrency)
                    binding.toCurrencyLogo.setBackgroundResource(resourceImage)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun fetchAndPopulateCurrencies() {
        val call = RetrofitClientRates.apiService.getLatestRates()
        call.enqueue(object : Callback<OpenExchangeRatesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<OpenExchangeRatesResponse>,
                response: Response<OpenExchangeRatesResponse>
            ) {
                if (response.isSuccessful) {
                    val rates = response.body()?.rates

                    val currencyCodes = rates?.keys?.sorted()?.toMutableList() ?: mutableListOf()
                    val currenciesCrypto =
                        arrayOf("BTC", "ETH", "BNB", "PEPE", "SOL", "DOGE", "WIF")
                    currencyCodes.addAll(currenciesCrypto)
                    initSpinners(currencyCodes)

                } else {
                    Log.e("ConverterActivity", "Помилка: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OpenExchangeRatesResponse>, t: Throwable) {
                Log.e("ConverterActivity", "Помилка: ${t.message}")
            }
        })
    }

    private fun initSpinners(listCurrencies: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listCurrencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.fromCurrencySpinner.adapter = adapter
        binding.toCurrencySpinner.adapter = adapter
    }

    private fun changeImageSelectedSpinnersItem(currency: String): Int {
        val imageResource = when (currency.uppercase()) {
            "USD" -> R.drawable.usd_logo_circle
            "BTC" -> R.drawable.bitcoin_logo_circle
            "ETH" -> R.drawable.eth_logo_circle
            "BNB" -> R.drawable.bnb_logo
            "SOL" -> R.drawable.sol_logo
            "PEPE" -> R.drawable.pepe_ico
            "WIF" -> R.drawable.wif_logo
            "DOGE" -> R.drawable.doge_logo
            else -> R.drawable.usd_logo_circle
        }
        return imageResource
    }

    private fun convertCurrency() {
        val amountString = binding.amountEditText.text.toString()
        val fromCurrency = binding.fromCurrencySpinner.selectedItem.toString()
        val toCurrency = binding.toCurrencySpinner.selectedItem.toString()

        if (amountString.isEmpty()) {
            Toast.makeText(this, "Введіть суму", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountString.toDoubleOrNull() ?: 0.0

        if (fromCurrency == toCurrency) {
            Toast.makeText(this, "Конвертація в ту саму валюту", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val rate = getExchangeRate(fromCurrency, toCurrency)

            withContext(Dispatchers.Main) {
                if (rate != null) {
                    val convertedAmount = amount * rate
                    val resultText = String.format(
                        "%.2f %s = %.2f %s",
                        amount,
                        fromCurrency,
                        convertedAmount,
                        toCurrency
                    )
                    binding.resultTextView.text = resultText
                } else {
                    Toast.makeText(
                        this@ConverterActivity,
                        "Помилка отримання курсу обміну",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ConverterActivity", "Помилка отримання курсу обміну")
                }
            }
        }
    }

    private suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Double? {
        return withContext(Dispatchers.IO) {
            if (isCryptoCurrency(fromCurrency) || isCryptoCurrency(toCurrency)) {
                getCryptoCurrencyRate(fromCurrency, toCurrency)
            } else {
                getFiatCurrencyRate(fromCurrency, toCurrency)
            }
        }
    }

    private fun isCryptoCurrency(currency: String): Boolean {
        val cryptoCurrencies = listOf("BTC", "ETH", "BNB", "PEPE", "SOL", "DOGE", "WIF")
        return currency.uppercase() in cryptoCurrencies
    }

    private fun getCryptoCurrencyRate(fromCurrency: String, toCurrency: String): Double? {
        val call = RetrofitClient.apiService.getCryptocurrencyPrices(
            fromSymbols = fromCurrency,
            toSymbols = toCurrency
        )

        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val data = response.body()?.RAW
                val currencyData = data?.get(fromCurrency)
                currencyData?.get(toCurrency)?.PRICE
            } else {
                Log.e("ConverterActivity", "CryptoCompare API Error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ConverterActivity", "Network Error: ${e.message}")
            null
        }
    }

    private fun getFiatCurrencyRate(fromCurrency: String, toCurrency: String): Double? {
        val call = RetrofitClientRates.apiService.getLatestRates()

        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val data = response.body()
                val baseCurrency = data?.base ?: "USD"
                val fromRate = data?.rates?.get(fromCurrency) ?: 1.0
                val toRate = data?.rates?.get(toCurrency) ?: 1.0

                if (fromCurrency != baseCurrency) {
                    toRate / fromRate
                } else {
                    toRate
                }
            } else {
                Log.e("ConverterActivity", "Open Exchange Rates API Error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ConverterActivity", "Network Error: ${e.message}")
            null
        }
    }
}