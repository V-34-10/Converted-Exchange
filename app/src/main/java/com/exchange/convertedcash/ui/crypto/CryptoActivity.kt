package com.exchange.convertedcash.ui.crypto

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.exchange.convertedcash.R
import com.exchange.convertedcash.adapters.CryptocurrencyAdapter
import com.exchange.convertedcash.retrofit.crypto.RetrofitClient
import com.exchange.convertedcash.databinding.ActivityMenuBinding
import com.exchange.convertedcash.retrofit.crypto.model.CryptoCompareResponse
import com.exchange.convertedcash.retrofit.crypto.model.CryptoListResponse
import com.exchange.convertedcash.model.Cryptocurrency
import com.exchange.convertedcash.ui.converter.ConverterActivity
import com.exchange.convertedcash.ui.menu.MenuCategoryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CryptoActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMenuBinding.inflate(layoutInflater) }
    private lateinit var adapter: CryptocurrencyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        binding.progressBar.visibility = View.VISIBLE
        binding.progressBar.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.progress_animation
            )
        )

        initCryptoAdapter()
        navigateButton()
    }

    private fun navigateButton() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.scale_btn)
        binding.btnConvert.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@CryptoActivity, ConverterActivity::class.java))
            finish()
        }
        binding.btnBack.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@CryptoActivity, MenuCategoryActivity::class.java))
            finish()
        }
    }

    private fun initCryptoAdapter() {
        val cryptoSymbols = "BTC,ETH,BNB,PEPE,SOL,DOGE,WIF,XRP"

        val listCall = RetrofitClient.apiService.getCryptocurrencyList()
        listCall.enqueue(object : Callback<CryptoListResponse> {
            override fun onResponse(
                call: Call<CryptoListResponse>,
                response: Response<CryptoListResponse>
            ) {
                if (response.isSuccessful) {
                    val cryptoData = response.body()?.Data ?: emptyMap()
                    Log.d("MenuActivity", "Список криптовалют: ${response.body()}")
                    val priceCall =
                        RetrofitClient.apiService.getCryptocurrencyPrices(fromSymbols = cryptoSymbols)
                    priceCall.enqueue(object : Callback<CryptoCompareResponse> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(
                            call: Call<CryptoCompareResponse>,
                            priceResponse: Response<CryptoCompareResponse>
                        ) {
                            if (priceResponse.isSuccessful) {
                                val priceData = priceResponse.body()?.RAW
                                val cryptoList = mutableListOf<Cryptocurrency>()
                                Log.d("MenuActivity", "Список криптовалют: ${priceResponse.body()}")
                                priceData?.forEach { (symbol, data) ->
                                    val usdPrice = data["USD"]?.PRICE

                                    val fullName = cryptoData[symbol]?.FullName ?: symbol
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
                                binding.progressBar.visibility = View.GONE
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