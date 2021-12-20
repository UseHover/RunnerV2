package com.hover.runner.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import com.hover.runner.R
import com.hover.runner.filter.enumValue.FilterForEnum
import com.hover.runner.filter.filter_actions.navigation.FilterActionNavigationInterface
import com.hover.runner.filter.filter_transactions.navigation.FilterTransactionNavigationInterface
import com.hover.runner.transaction.navigation.TransactionNavigationInterface
import com.hover.runner.webview.WebViewActivity

abstract class AbstractNavigationActivity : SDKCallerActivity(),
	TransactionNavigationInterface,	FilterActionNavigationInterface, FilterTransactionNavigationInterface {
	private lateinit var navController: NavController

	override fun navActionDetails(actionId: String, titleTextView: View) {
		ViewCompat.setTransitionName(titleTextView, "action_title")
		val bundle = Bundle()
		bundle.putString("action_id", actionId)
		navController.navigate(R.id.navigation_actionDetails, bundle, null, null)
	}

	fun navWebView(title: String, url: String) {
		val i = Intent(this, WebViewActivity::class.java)
		i.putExtra(WebViewActivity.TITLE, title)
		i.putExtra(WebViewActivity.URL, url)
		startActivity(i)
	}

	override fun navigateTransactionFilterFragment() {
		navController.navigate(R.id.navigation_transactionsFilter)
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