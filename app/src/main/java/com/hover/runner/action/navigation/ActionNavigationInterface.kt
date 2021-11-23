package com.hover.runner.action.navigation

import android.view.View

interface ActionNavigationInterface {
    fun navActionDetails(actionId: String, titleTextView: View)
    fun navWebView(title: String, url: String)
    fun navParserDetailsFragment(actionId: String, parserId: Int)
    fun navTransactionDetails(uuid: String)
    fun navTransactionListFragment(filterByActionId: String)
    fun navUnCompletedVariableFragment()
}