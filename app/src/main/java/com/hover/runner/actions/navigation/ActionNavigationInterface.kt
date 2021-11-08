package com.hover.runner.actions.navigation

import android.view.View
import androidx.navigation.NavController

interface ActionNavigationInterface {
    fun navActionDetails(actionId: String, titleTextView: View)
    fun navWebView(title: String, url: String)
}