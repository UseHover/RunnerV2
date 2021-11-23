package com.hover.runner.parser.navigation

import android.view.View

interface ParserNavigationInterface {
    fun navActionDetails(actionId: String, titleTextView: View)
    fun navTransactionDetails(uuid: String)
    fun navParserDetailsFragment(actionId: String, parserId: Int)
}