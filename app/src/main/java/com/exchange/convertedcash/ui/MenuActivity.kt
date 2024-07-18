package com.exchange.convertedcash.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.exchange.convertedcash.R
import com.exchange.convertedcash.adapters.CryptocurrencyAdapter
import com.exchange.convertedcash.crypto.RetrofitClient
import com.exchange.convertedcash.databinding.ActivityMenuBinding
import com.exchange.convertedcash.crypto.CryptoCompareResponse
import com.exchange.convertedcash.crypto.CryptoListResponse
import com.exchange.convertedcash.model.Cryptocurrency
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMenuBinding.inflate(layoutInflater) }
    private lateinit var adapter: CryptocurrencyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        setCryptoAdapter()
        navigateButton()
    }

    private fun navigateButton() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.scale_btn)
        binding.btnConvert.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@MenuActivity, ConverterActivity::class.java))
            finish()
        }
        binding.btnBack.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@MenuActivity, MenuCategoryActivity::class.java))
            finish()
        }
    }

    private fun setCryptoAdapter() {
        val cryptoSymbols = "BTC,ETH,BNB,PEPE,SOL,DOGE,WIF"
        /*val call = RetrofitClient.apiService.getCryptocurrencyPrices(fromSymbols = cryptoSymbols)

        call.enqueue(object : Callback<CryptoCompareResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<CryptoCompareResponse>,
                response: Response<CryptoCompareResponse>
            ) {
                if (response.isSuccessful) {
                    val cryptoData = response.body()?.raw
                    val cryptoList = mutableListOf<Cryptocurrency>()

                    cryptoData?.forEach { (symbol, data) ->
                        val usdPrice = data["USD"]?.price
                        cryptoList.add(
                            Cryptocurrency(
                                symbol = symbol,
                                name = symbol,
                                usdPrice = usdPrice
                            )
                        )
                    }
                    adapter = CryptocurrencyAdapter(cryptoList)
                    binding.listCurrency.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("MainActivity", "Помилка: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CryptoCompareResponse>, t: Throwable) {
                Log.e("MainActivity", "Помилка: ${t.message}")
            }
        })*/

        val listCall = RetrofitClient.apiService.getCryptocurrencyList()
        listCall.enqueue(object : Callback<CryptoListResponse> {
            override fun onResponse(
                call: Call<CryptoListResponse>,
                response: Response<CryptoListResponse>
            ) {
                if (response.isSuccessful) {
                    val cryptoData = response.body()?.data ?: emptyMap()

                    val priceCall =
                        RetrofitClient.apiService.getCryptocurrencyPrices(fromSymbols = cryptoSymbols)
                    priceCall.enqueue(object : Callback<CryptoCompareResponse> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(
                            call: Call<CryptoCompareResponse>,
                            priceResponse: Response<CryptoCompareResponse>
                        ) {
                            if (priceResponse.isSuccessful) {
                                val priceData = priceResponse.body()?.raw
                                val cryptoList = mutableListOf<Cryptocurrency>()

                                priceData?.forEach { (symbol, data) ->
                                    val usdPrice = data["USD"]?.price

                                    val fullName = cryptoData[symbol]?.fullName ?: symbol
                                    cryptoList.add(
                                        Cryptocurrency(
                                            symbol = symbol,
                                            name = fullName,
                                            usdPrice = usdPrice
                                        )
                                    )
                                }
                                adapter = CryptocurrencyAdapter(cryptoList)
                                binding.listCurrency.adapter = adapter
                                adapter.notifyDataSetChanged()
                            } else {
                                Log.e("MenuActivity", "Помилка цін: ${priceResponse.code()}")
                            }
                        }

                        override fun onFailure(call: Call<CryptoCompareResponse>, t: Throwable) {
                            Log.e("MenuActivity", "Помилка цін: ${t.message}")
                        }
                    })
                } else {
                    Log.e("MenuActivity", "Помилка списку: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CryptoListResponse>, t: Throwable) {
                Log.e("MenuActivity", "Помилка списку: ${t.message}")
            }
        })
    }

}