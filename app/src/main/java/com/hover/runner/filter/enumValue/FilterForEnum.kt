package com.hover.runner.filter.enumValue

enum class FilterForEnum {
	ACTIONS, TRANSACTIONS;

	fun isForActions(): Boolean {
		return this == ACTIONS
	}

	fun isForTransactions(): Boolean {
		return this == TRANSACTIONS
	}
}
