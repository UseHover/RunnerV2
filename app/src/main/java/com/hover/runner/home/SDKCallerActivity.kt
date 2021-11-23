package com.hover.runner.home

import android.os.Bundle
import android.os.Handler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.hover.runner.R
import com.hover.runner.action.models.ActionVariablesCache
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.parser.viewmodel.ParserViewModel
import com.hover.runner.settings.fragment.SettingsFragment
import com.hover.runner.settings.viewmodel.SettingsViewModel
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import com.hover.runner.utils.SharedPrefUtils
import com.hover.sdk.api.HoverParameters
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

abstract class SDKCallerActivity : AppCompatActivity(), SDKCallerInterface {
    private val actionViewModel: ActionViewModel by viewModel()
    private val transactionViewModel: TransactionViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val parserViewModel: ParserViewModel by viewModel()

    private var lastRanPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeActionViewModel()
    }
    private fun observeActionViewModel() {
        Timber.i("listening to some action view models")
        //Necessary to observe these liveData so transformations can work
        actionViewModel.actionsWithUCV_LiveData.observe(this) {
            Timber.i("listening to actions with UC variables")
        }
        actionViewModel.actionsWithCompletedVariables.observe(this) {
            Timber.i("listening to actions with completed variables")
        }
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

    override fun runChainedActions() {
        try{
            lastRanPos += 1
            val actions = actionViewModel.getRunnableActions()
            if (lastRanPos >= actions.size) return
            else {
                val nextAction = actions[lastRanPos]
                val builder = initBuilder(nextAction.id)
                if (lastRanPos != actions.size - 1) builder.finalMsgDisplayTime(0)

                val chainedActionLauncher =
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == RESULT_OK) {
                            runChainedActions()
                        }
                    }
                var throttle = 0
                if (lastRanPos > 0) {
                    throttle = SharedPrefUtils.getDelay(this)
                }
                Handler().postDelayed(
                    { chainedActionLauncher.launch(builder.buildIntent()) },
                    throttle.toLong()
                )
            }
        }catch (e: IllegalStateException) {
            Timber.e(e)
        }

    }

    override fun runAction(actionId: String) {
        startActivity(initBuilder(actionId).buildIntent())
    }

}