package com.exchange.convertedcash.ui.crypto

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.exchange.convertedcash.adapters.CryptocurrencyAdapter
import com.exchange.convertedcash.databinding.ActivityMenuBinding
import com.exchange.convertedcash.model.Cryptocurrency
import com.exchange.convertedcash.retrofit.crypto.RetrofitClient
import com.exchange.convertedcash.retrofit.crypto.model.CryptoCompareResponse
import com.exchange.convertedcash.retrofit.crypto.model.CryptoInfo
import com.exchange.convertedcash.retrofit.crypto.model.CryptoListResponse
import com.exchange.convertedcash.ui.converter.ConverterActivity
import com.exchange.convertedcash.ui.menu.MenuCategoryActivity
import com.exchange.convertedcash.utils.AnimationManager.startAnimateClickButton
import com.exchange.convertedcash.utils.AnimationManager.startAnimationProgressBarForResponse
import com.exchange.convertedcash.utils.NavigationManager.navigateToActivity
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
        startAnimationProgressBarForResponse(binding.progressBar)
        callResponseFromCryptoService()
        navigateButton()
    }

    private fun navigateButton() {
        binding.btnConvert.setOnClickListener {
            startAnimateClickButton(it, this)
            navigateToActivity(ConverterActivity::class.java, this)
        }
        binding.btnBack.setOnClickListener {
            startAnimateClickButton(it, this)
            navigateToActivity(MenuCategoryActivity::class.java, this)
        }
    }

    private fun callResponseFromCryptoService() {
        val cryptoSymbols = "BTC,ETH,BNB,PEPE,SOL,DOGE,WIF,XRP,TON,LTC"
        val listCall = RetrofitClient.apiService.getCryptocurrencyList()
        listCall.enqueue(object : Callback<CryptoListResponse> {
            override fun onResponse(
                call: Call<CryptoListResponse>,
                response: Response<CryptoListResponse>
            ) {
                if (response.isSuccessful) {
                    val cryptoData = response.body()?.Data ?: emptyMap()
                    Log.d("CryptoActivity", "List of cryptocurrencies: ${response.body()}")
                    val priceCall =
                        RetrofitClient.apiService.getCryptocurrencyPrices(fromSymbols = cryptoSymbols)
                    priceCall.enqueue(object : Callback<CryptoCompareResponse> {
                        override fun onResponse(
                            call: Call<CryptoCompareResponse>,
                            priceResponse: Response<CryptoCompareResponse>
                        ) {
                            if (priceResponse.isSuccessful) {
                                successResponsePrice(priceResponse, cryptoData)
                            } else {
                                Log.e("CryptoActivity", "Price mistake: ${priceResponse.code()}")
                            }
                        }

                        override fun onFailure(call: Call<CryptoCompareResponse>, t: Throwable) {
                            Log.e("CryptoActivity", "Price mistake: ${t.message}")
                        }
                    })
                } else {
                    Log.e("CryptoActivity", "Error List: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CryptoListResponse>, t: Throwable) {
                Log.e("CryptoActivity", "Error List: ${t.message}")
            }
        })
    }

    private fun successResponsePrice(
        priceResponse: Response<CryptoCompareResponse>,
        cryptoData: Map<String, CryptoInfo>
    ) {
        val priceData = priceResponse.body()?.RAW
        val cryptoList = mutableListOf<Cryptocurrency>()
        Log.d("CryptoActivity", "List of cryptocurrencies: ${priceResponse.body()}")
        priceData?.forEach { (symbol, data) ->
            cryptoList.add(
                Cryptocurrency(
                    symbol = symbol,
                    name = cryptoData[symbol]?.FullName ?: symbol,
                    usdPrice = data["USD"]?.PRICE
                )
            )
        }
        initAdapterCrypto(cryptoList)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapterCrypto(cryptoList: List<Cryptocurrency>) {
        adapter = CryptocurrencyAdapter(cryptoList)
        binding.listCurrency.adapter = adapter
        adapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.GONE
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CryptoActivity, MenuCategoryActivity::class.java))
        finish()
    }
}