package com.hover.runner.filter.filter_actions.navigation

import com.hover.runner.filter.enumValue.FilterForEnum

interface FilterActionNavigationInterface {
	fun navigateToSelectCategoriesFragment()
	fun navigateToSelectCountriesFragment(filterForEnum: FilterForEnum)
	fun navigateToSelectNetworksFragment(filterForEnum: FilterForEnum)
}