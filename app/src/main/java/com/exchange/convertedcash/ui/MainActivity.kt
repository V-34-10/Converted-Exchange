package com.exchange.convertedcash.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.exchange.convertedcash.R
import com.exchange.convertedcash.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val activityScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
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

        activityScope.launch {
            delay(3000L)
            startActivity(Intent(this@MainActivity, MenuCategoryActivity::class.java))
            binding.progressBar.visibility = View.GONE
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }
}