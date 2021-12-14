package com.hover.runner.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.hover.runner.R
import com.hover.runner.actions.ActionsViewModel
import com.hover.runner.parser.viewmodel.ParserViewModel
import com.hover.runner.settings.fragment.SettingsFragment
import com.hover.runner.sim.viewmodel.SimViewModel
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.SharedPrefUtils
import com.hover.sdk.api.HoverParameters
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

abstract class SDKCallerActivity : AppCompatActivity(), SDKCallerInterface {
	private val actionsViewModel: ActionsViewModel by viewModel()
	private val transactionViewModel: TransactionViewModel by viewModel()
	private val simViewModel: SimViewModel by viewModel()
	private val parserViewModel: ParserViewModel by viewModel()

	private var lastRanPos = -1

	private lateinit var chainedActionLauncher: ActivityResultLauncher<Intent>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		observeLiveData()
		initChainedLauncher()
	}

	override fun runAction(actionId: String) {
		startActivity(initBuilder(actionId).buildIntent())
	}

	private fun initChainedLauncher() {
		chainedActionLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
				runChainedActions()
			}
	}

	private fun getThrottle(): Int {
		return if (lastRanPos > 0) SharedPrefUtils.getDelay(this) else 0
	}

	private fun initBuilder(actionId: String): HoverParameters.Builder {
//		val actionExtras: Map<String, String> = ActionVariablesCache.get(this, actionId).actionMap
		val builder = HoverParameters.Builder(this)
		builder.request(actionId)
		builder.setEnvironment(SettingsFragment.getCurrentEnv(this))
		builder.style(R.style.myHoverTheme)
//		actionExtras.keys.forEach { builder.extra(it, actionExtras[it]) }
		return builder
	}

	private fun observeLiveData() {
		observeActionViewModel()
		observeTransactionMediators()
	}

	private fun observeActionViewModel() { //Necessary to observe these liveData for transformations to work
		actionsViewModel.incompleteActions.observe(this) {
			Timber.i("listening to actions with UC variables ${it.size}")
		}
		actionsViewModel.completedActions.observe(this) {
			Timber.i("listening to actions with completed variables ${it.size}")
		}
	}

	private fun observeTransactionMediators() {
		transactionViewModel.filterParameters_toFind_FilteredTransactions_MediatorLiveData.observe(
			this) { param ->
			param?.let { Timber.i("listing : ${it.actionIdList}") }

		}
	}

}