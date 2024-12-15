package com.exchange.convertedcash.ui.converter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exchange.convertedcash.R
import com.exchange.convertedcash.databinding.ActivityConverterBinding
import com.exchange.convertedcash.retrofit.crypto.RetrofitClient
import com.exchange.convertedcash.retrofit.fiat.RetrofitClientRates
import com.exchange.convertedcash.retrofit.fiat.model.OpenExchangeRatesResponse
import com.exchange.convertedcash.ui.menu.MenuCategoryActivity
import com.exchange.convertedcash.utils.AnimationManager.startAnimateClickButton
import com.exchange.convertedcash.utils.NavigationManager.navigateToActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConverterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityConverterBinding.inflate(layoutInflater) }
    private val cryptoCurrencies =
        listOf("BTC", "ETH", "BNB", "PEPE", "SOL", "DOGE", "WIF", "XRP", "TON", "LTC")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        fetchAndPopulateCurrencies()
        controlButton()
    }

    private fun controlButton() {
        binding.convertButton.setOnClickListener {
            startAnimateClickButton(it, this)
            convertCurrency()
        }
        binding.btnBack.setOnClickListener {
            startAnimateClickButton(it, this)
            navigateToActivity(MenuCategoryActivity::class.java, this)
        }
        binding.fromCurrencySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) = initItemSelectedSpinner(parent, position, binding.fromCurrencyLogo)

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        binding.toCurrencySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) = initItemSelectedSpinner(parent, position, binding.toCurrencyLogo)

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun initItemSelectedSpinner(
        parent: AdapterView<*>,
        position: Int,
        currencyLogo: ImageView
    ) {
        val selectedCurrency = parent.getItemAtPosition(position).toString()
        val resourceImage = changeImageSelectedSpinnersItem(selectedCurrency)
        currencyLogo.setBackgroundResource(resourceImage)
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
                    currencyCodes.addAll(cryptoCurrencies)
                    initSpinners(currencyCodes)

                } else Log.e("ConverterActivity", "Error: ${response.code()}")
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
            "XRP" -> R.drawable.xrp_logo
            "TON" -> R.drawable.ton_logo
            "LTC" -> R.drawable.ltc_logo
            else -> R.drawable.usd_logo_circle
        }
        return imageResource
    }

    @SuppressLint("DefaultLocale")
    private fun convertCurrency() {
        val amountString = binding.amountEditText.text.toString()
        val fromCurrency = binding.fromCurrencySpinner.selectedItem.toString()
        val toCurrency = binding.toCurrencySpinner.selectedItem.toString()

        if (amountString.isEmpty()) {
            Toast.makeText(this, "Enter the amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountString.toDoubleOrNull() ?: 0.0

        if (fromCurrency == toCurrency) {
            Toast.makeText(this, "Conversion to the same currency", Toast.LENGTH_SHORT).show()
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
                } else Toast.makeText(
                    this@ConverterActivity,
                    "Error in receiving the exchange rate",
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun isCryptoCurrency(currency: String): Boolean =
        currency.uppercase() in cryptoCurrencies

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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        navigateToActivity(MenuCategoryActivity::class.java, this)
    }
}