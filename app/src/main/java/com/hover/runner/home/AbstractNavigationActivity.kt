package com.hover.runner.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hover.runner.R
import com.hover.runner.actions.navigation.ActionNavigationInterface
import com.hover.runner.webview.WebViewActivity

abstract class AbstractNavigationActivity : AppCompatActivity() , ActionNavigationInterface {
    private lateinit var navController : NavController

    fun setupNavigation() {
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navView, navController)

        if (intent.extras != null) {
            if (intent.extras!!.getString("navigate") != null) {
                navController.navigate(R.id.navigation_transactions)
            }
        }
    }

    override fun navActionDetails(actionId: String, titleTextView: View) {
        val bundle = Bundle()
        bundle.putString("action_id", actionId)
        val extras = FragmentNavigatorExtras(titleTextView to "action_title")
        navController.navigate(R.id.navigation_actionDetails, bundle, null, extras)
    }

    override fun navWebView(title: String, url: String) {
        val i = Intent(this, WebViewActivity::class.java)
        i.putExtra(WebViewActivity.TITLE, title)
        i.putExtra(WebViewActivity.URL, url)
        startActivity(i)
    }


}