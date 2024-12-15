package com.exchange.convertedcash.ui.fiat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.exchange.convertedcash.adapters.CurrencyAdapter
import com.exchange.convertedcash.databinding.ActivityFiatBinding
import com.exchange.convertedcash.model.Currency
import com.exchange.convertedcash.retrofit.fiat.RetrofitClientRates
import com.exchange.convertedcash.retrofit.fiat.model.OpenExchangeRatesResponse
import com.exchange.convertedcash.ui.converter.ConverterActivity
import com.exchange.convertedcash.ui.menu.MenuCategoryActivity
import com.exchange.convertedcash.utils.AnimationManager.startAnimateClickButton
import com.exchange.convertedcash.utils.AnimationManager.startAnimationProgressBarForResponse
import com.exchange.convertedcash.utils.NavigationManager.navigateToActivity
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
        startAnimationProgressBarForResponse(binding.progressBar)
        callResponseFromService()
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

    private fun callResponseFromService() {
        val call = RetrofitClientRates.apiService.getLatestRates()
        call.enqueue(object : Callback<OpenExchangeRatesResponse> {
            override fun onResponse(
                call: Call<OpenExchangeRatesResponse>,
                response: Response<OpenExchangeRatesResponse>
            ) {
                if (response.isSuccessful) {
                    successResponse(response)
                } else {
                    Log.e("FiatActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OpenExchangeRatesResponse>, t: Throwable) {
                Log.e("FiatActivity", "Error: ${t.message}")
            }
        })
    }

    private fun successResponse(response: Response<OpenExchangeRatesResponse>) {
        val rates = response.body()?.rates
        val currencyList = mutableListOf<Currency>()
        rates?.forEach { (code, rate) -> currencyList.add(Currency(code = code, rate = rate)) }
        initAdapterFiat(currencyList)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapterFiat(currencyList: List<Currency>) {
        adapter = CurrencyAdapter(currencyList)
        binding.listCurrency.adapter = adapter
        adapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.GONE
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        navigateToActivity(MenuCategoryActivity::class.java, this)
    }
}