package com.hover.runner.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hover.runner.R
import com.hover.runner.actions.navigation.ActionNavigationInterface
import com.hover.runner.login.activities.LoginActivity
import com.hover.runner.parser.navigation.ParserNavigationInterface
import com.hover.runner.settings.navigation.SettingsNavigationInterface
import com.hover.runner.transactions.navigation.TransactionNavigationInterface
import com.hover.runner.webview.WebViewActivity

 abstract class AbstractNavigationActivity : SDKCallerActivity() ,
    ActionNavigationInterface,
    TransactionNavigationInterface,
    ParserNavigationInterface,
    SettingsNavigationInterface{
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
        ViewCompat.setTransitionName(titleTextView, "action_title");
        val bundle = Bundle()
        bundle.putString("action_id", actionId)
        navController.navigate(R.id.navigation_actionDetails, bundle, null, null)
    }

    override fun navWebView(title: String, url: String) {
        val i = Intent(this, WebViewActivity::class.java)
        i.putExtra(WebViewActivity.TITLE, title)
        i.putExtra(WebViewActivity.URL, url)
        startActivity(i)
    }

     override fun navParserDetailsFragment(parserId: Int) {
         val bundle = Bundle()
         bundle.putInt("parser_id", parserId)
         navController.navigate(R.id.navigation_actionDetails, bundle, null, null)
     }

     override fun navTransactionListFragment(filterByActionId: String) {
         TODO("Not yet implemented because filtering is yet to be done")
     }

    override fun navUnCompletedVariableFragment() {
        navController.navigate(R.id.navigation_uncompletedVariableFragment)
    }

    override fun navTransactionDetails(uuid: String) {
        val bundle = Bundle()
        bundle.putString("uuid", uuid)
        navController.navigate(R.id.navigation_transactionDetails, bundle, null, null)
     }

     override fun navLoginAndFinish() {
         startActivity(Intent(this, LoginActivity::class.java))
     }
 }