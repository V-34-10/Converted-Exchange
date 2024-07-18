package com.exchange.convertedcash.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.exchange.convertedcash.R
import com.exchange.convertedcash.adapters.CurrencyAdapter
import com.exchange.convertedcash.databinding.ActivityFiatBinding
import com.exchange.convertedcash.fiat.OpenExchangeRatesResponse
import com.exchange.convertedcash.fiat.RetrofitClientRates
import com.exchange.convertedcash.model.Currency
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FiatActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFiatBinding.inflate(layoutInflater) }
    private lateinit var adapter: CurrencyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        setCurrencyAdapter()
        navigateButton()
    }

    private fun navigateButton() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.scale_btn)
        binding.btnConvert.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@FiatActivity, ConverterActivity::class.java))
            finish()
        }
        binding.btnBack.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@FiatActivity, MenuCategoryActivity::class.java))
            finish()
        }
    }

    private fun setCurrencyAdapter() {
        val call = RetrofitClientRates.apiService.getLatestRates()
        call.enqueue(object : Callback<OpenExchangeRatesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<OpenExchangeRatesResponse>,
                response: Response<OpenExchangeRatesResponse>
            ) {
                if (response.isSuccessful) {
                    val rates = response.body()?.rates
                    val currencyList = mutableListOf<Currency>()

                    rates?.forEach { (code, rate) ->
                        currencyList.add(Currency(code = code, rate = rate))
                    }

                    adapter = CurrencyAdapter(currencyList)
                    binding.listCurrency.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("FiatActivity", "Помилка: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OpenExchangeRatesResponse>, t: Throwable) {
                Log.e("FiatActivity", "Помилка: ${t.message}")
            }
        })
    }
}