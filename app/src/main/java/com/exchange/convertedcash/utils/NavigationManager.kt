package com.exchange.convertedcash.utils

import android.app.Activity
import android.content.Intent

object NavigationManager {

    fun <T> navigateToActivity(activityClass: Class<T>, activity: Activity) {
        activity.startActivity(Intent(activity, activityClass))
        activity.finish()
    }
}