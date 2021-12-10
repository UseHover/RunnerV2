package com.hover.runner.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hover.runner.R
import com.hover.runner.action.navigation.ActionNavigationInterface
import com.hover.runner.filter.enumValue.FilterForEnum
import com.hover.runner.filter.filter_actions.navigation.FilterActionNavigationInterface
import com.hover.runner.filter.filter_transactions.navigation.FilterTransactionNavigationInterface
import com.hover.runner.login.activities.LoginActivity
import com.hover.runner.parser.navigation.ParserNavigationInterface
import com.hover.runner.settings.navigation.SettingsNavigationInterface
import com.hover.runner.transaction.navigation.TransactionNavigationInterface
import com.hover.runner.webview.WebViewActivity

abstract class AbstractNavigationActivity : SDKCallerActivity(), ActionNavigationInterface,
	TransactionNavigationInterface, ParserNavigationInterface, SettingsNavigationInterface,
	FilterActionNavigationInterface, FilterTransactionNavigationInterface {
	private lateinit var navController: NavController

	fun setupNavigation() {
		val navView = findViewById<BottomNavigationView>(R.id.nav_view)
		navController = Navigation.findNavController(this, R.id.nav_host_fragment)
		NavigationUI.setupWithNavController(navView, navController)

		if (intent.extras != null && intent.extras!!.getString("navigate") != null) {
			navController.navigate(R.id.navigation_transactions)
		}
	}

	override fun navActionDetails(actionId: String, titleTextView: View) {
		ViewCompat.setTransitionName(titleTextView, "action_title")
		val bundle = Bundle()
		bundle.putString("action_id", actionId)
		navController.navigate(R.id.navigation_actionDetails, bundle, null, null)
	}

	override fun navUnCompletedVariableFragment() {
		navController.navigate(R.id.navigation_uncompletedVariableFragment)
	}

	override fun navActionFilterFragment() {
		navController.navigate(R.id.navigation_actionFilter)
	}

	override fun navLoginAndFinish() {
		startActivity(Intent(this, LoginActivity::class.java))
	}

	override fun navWebView(title: String, url: String) {
		val i = Intent(this, WebViewActivity::class.java)
		i.putExtra(WebViewActivity.TITLE, title)
		i.putExtra(WebViewActivity.URL, url)
		startActivity(i)
	}

	override fun navParserDetailsFragment(actionId: String, parserId: Int) {
		val bundle = Bundle()
		bundle.putInt("parser_id", parserId)
		bundle.putString("action_id", actionId)
		navController.navigate(R.id.navigation_parserDetails, bundle, null, null)
	}

	override fun navigateTransactionFilterFragment() {
		navController.navigate(R.id.navigation_transactionsFilter)
	}

	override fun navTransactionListFragment(filterByActionId: String) {
		TODO("Not yet implemented")
	}

	override fun navTransactionDetails(uuid: String) {
		val bundle = Bundle()
		bundle.putString("uuid", uuid)
		navController.navigate(R.id.navigation_transactionDetails, bundle, null, null)
	}

	override fun navigateToSelectActionsFragment() {
		navController.navigate(R.id.navigation_filter_selectByActions)
	}

	override fun navigateToSelectCategoriesFragment() {
		navController.navigate(R.id.navigation_filter_selectByCategory)
	}

	override fun navigateToSelectCountriesFragment(filterForEnum: FilterForEnum) {
		val bundle = Bundle()
		if (filterForEnum.isForActions()) bundle.putInt("filterFor", 0)
		else if (filterForEnum.isForTransactions()) bundle.putInt("filterFor", 1)
		navController.navigate(R.id.navigation_filter_selectByCountry, bundle, null, null)
	}

	override fun navigateToSelectNetworksFragment(filterForEnum: FilterForEnum) {
		val bundle = Bundle()
		if (filterForEnum.isForActions()) bundle.putInt("filterFor", 0)
		else if (filterForEnum.isForTransactions()) bundle.putInt("filterFor", 1)
		navController.navigate(R.id.navigation_filter_selectByNetworkNames, bundle, null, null)
	}
}