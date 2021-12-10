package com.hover.runner.filter.filter_transactions.navigation

import com.hover.runner.filter.enumValue.FilterForEnum

interface FilterTransactionNavigationInterface {
    fun navigateToSelectActionsFragment()
    fun navigateToSelectCountriesFragment(filterForEnum: FilterForEnum)
    fun navigateToSelectNetworksFragment(filterForEnum: FilterForEnum)
}