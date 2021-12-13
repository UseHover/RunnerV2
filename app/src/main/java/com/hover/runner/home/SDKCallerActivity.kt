package com.hover.runner.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hover.runner.R
import com.hover.runner.action.models.ActionVariablesCache
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.parser.viewmodel.ParserViewModel
import com.hover.runner.settings.fragment.SettingsFragment
import com.hover.runner.sim.viewmodel.SimViewModel
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.SharedPrefUtils
import com.hover.sdk.api.HoverParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

abstract class SDKCallerActivity : AppCompatActivity(), SDKCallerInterface {
	private val actionViewModel: ActionViewModel by viewModel()
	private var lastRanPos = -1
	private lateinit var chainedActionLauncher: ActivityResultLauncher<Intent>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initChainedLauncher()
	}

	override fun runChainedActions() {
		try {
			lastRanPos += 1
			val actions = actionViewModel.getRunnableActions()
			if (lastRanPos >= actions.size) return
			else {
				val nextAction = actions[lastRanPos]
				val builder = initBuilder(nextAction.id)
				if (lastRanPos != actions.size - 1) builder.finalMsgDisplayTime(0)

				lifecycleScope.launch(Dispatchers.Main) {
					delay(getThrottle().toLong())
					chainedActionLauncher.launch(builder.buildIntent())
				}
			}
		} catch (e: IllegalStateException) {
			Timber.e(e)
		}

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
		val actionExtras: Map<String, String> = ActionVariablesCache.get(this, actionId).actionMap
		val builder = HoverParameters.Builder(this)
		builder.request(actionId)
		builder.setEnvironment(SettingsFragment.getCurrentEnv(this))
		builder.style(R.style.myHoverTheme)
		actionExtras.keys.forEach { builder.extra(it, actionExtras[it]) }
		return builder
	}



}