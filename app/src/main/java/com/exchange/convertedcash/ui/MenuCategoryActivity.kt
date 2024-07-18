package com.exchange.convertedcash.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.exchange.convertedcash.R
import com.exchange.convertedcash.databinding.ActivityMenuCategoryBinding

class MenuCategoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMenuCategoryBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        navigateCategoryMenuButton()
    }

    private fun navigateCategoryMenuButton() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.scale_btn)
        binding.btnCategoryCryptocurrencies.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@MenuCategoryActivity, MenuActivity::class.java))
            finish()
        }
        binding.btnWorldCurrencies.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@MenuCategoryActivity, FiatActivity::class.java))
            finish()
        }
        binding.btnConvert.setOnClickListener {
            it.startAnimation(animation)
            startActivity(Intent(this@MenuCategoryActivity, ConverterActivity::class.java))
            finish()
        }
    }
}