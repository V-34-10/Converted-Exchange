package com.exchange.convertedcash.utils

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.exchange.convertedcash.R

object AnimationManager {
    fun startAnimationProgressBarForResponse(progressBar: ImageView) {
        progressBar.visibility = View.VISIBLE
        progressBar.startAnimation(
            AnimationUtils.loadAnimation(
                progressBar.context,
                R.anim.progress_animation
            )
        )
    }

    fun startAnimateClickButton(view: View, context: Context) = view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_btn))
}