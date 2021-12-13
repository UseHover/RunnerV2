package com.hover.runner.home

import android.os.Bundle
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.parser.viewmodel.ParserViewModel
import com.hover.runner.sim.viewmodel.SimViewModel
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

abstract class AbstractViewModelActivity : AbstractNavigationActivity() {
	private val actionViewModel: ActionViewModel by viewModel()
	private val transactionViewModel: TransactionViewModel by viewModel()
	private val simViewModel: SimViewModel by viewModel()
	private val parserViewModel: ParserViewModel by viewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		actionViewModel.getAllActions()
		observeLiveData()
	}
	private fun observeLiveData() {
		timberViewModelData()
		observeActionMediators()
		observeTransactionMediators()
	}

	private fun timberViewModelData() { //Necessary to observe these liveData for transformations to work
		actionViewModel.actionsWithUCV_LiveData.observe(this) {
			Timber.i("listening to actions with UC variables ${it.size}")
		}
		actionViewModel.actionsWithCompletedVariables.observe(this) {
			Timber.i("listening to actions with completed variables ${it.size}")
		}
		actionViewModel.loadingStatusLiveData.observe(this) {
			Timber.i("listening to loading status $it")
		}
		actionViewModel.actions.observe(this) {
			Timber.i("listening to actions ${it.size}")
		}
	}


	private fun observeActionMediators() {
		actionViewModel.allActions_toFind_BadActions_MediatorLiveData.observe(this) {
			Timber.i("listing to bad actions : ${it.size}")
		}
		actionViewModel.badActions_toFind_GoodActions_MediatorLiveData.observe(this) {
			Timber.i("listing to good actions : ${it.size}")
		}
		actionViewModel.filterParameters_toFind_FilteredActions_MediatorLiveData.observe(this) {
			Timber.i("listening to fitering data : ${it.actionIdList}")
		}
		actionViewModel.allNetworks_toLoad_countryCodes_MediatorLiveData.observe(this) {
			Timber.i("listing : ${it.size}")
		}
		actionViewModel.countryCodes_toFind_ItsNetworks_MediatorLiveData.observe(this) {
			Timber.i("listing  : ${it.size}")
		}
		actionViewModel.networksWithinCountry_toFind_networksOutsideCountry_MediatorLiveData.observe(
			this) {
			Timber.i("listing : ${it.size}")
		}
	}

	private fun observeTransactionMediators() {
		transactionViewModel.filterParameters_toFind_FilteredTransactions_MediatorLiveData.observe(
			this) { param ->
			param?.let { Timber.i("listing : ${it.actionIdList}") }

		}
	}
}