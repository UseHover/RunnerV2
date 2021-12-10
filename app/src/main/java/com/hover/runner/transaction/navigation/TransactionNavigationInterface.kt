package com.hover.runner.transaction.navigation

import android.view.View

interface TransactionNavigationInterface {
	fun navTransactionDetails(uuid: String)
	fun navActionDetails(actionId: String, titleTextView: View)
	fun navParserDetailsFragment(actionId: String, parserId: Int)
	fun navigateTransactionFilterFragment()
}