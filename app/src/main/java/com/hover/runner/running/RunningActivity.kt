package com.hover.runner.running

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import com.hover.runner.R
import com.hover.runner.databinding.ActivityTestRunningBinding
import com.hover.runner.settings.SettingsFragment
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.UIHelper
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.HoverParameters
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class RunningActivity : AppCompatActivity() {
	private lateinit var binding: ActivityTestRunningBinding

	private val viewModel: RunningViewModel by viewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		UIHelper.changeStatusBarColor(this, getColor(this, R.color.runnerPrimary))
		binding = ActivityTestRunningBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)

		viewModel.currentAction.observe(this) {
			if (it != null && !viewModel.isTestRunning() &&
					(savedInstanceState?.getString("running_action_id") == null ||
					savedInstanceState.getString("running_action_id") != it.public_id))
				startHover(it)
		}

		viewModel.run.observe(this) {
			it?.let { if ( viewModel.pendingActionIdList.value != null) updateProgressText() }
		}
		viewModel.pendingActionIdList.observe(this) {
			it?.let { if ( viewModel.run.value != null) updateProgressText() }
		}

		if (intent.hasExtra("runId"))
			viewModel.loadRun(intent.getLongExtra("runId", -1L))
	}

	private fun startHover(action: HoverAction) {
		Timber.e("Starting hover action %s", action.public_id)
		val builder = HoverParameters.Builder(this)
		builder.request(action.public_id)
		builder.setEnvironment(SettingsFragment.getCurrentEnv(this))
		builder.finalMsgDisplayTime(0)
		action.requiredParams.forEach { builder.extra(it, SharedPrefUtils.getVarValue(action.public_id, it, application)) }
		viewModel.setRunInProgress(true)
		startActivityForResult(builder.buildIntent(), 0)
	}

	@Override
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		Timber.e("Got result")
		if (data?.getStringExtra("action_id") != null) {
			Timber.e("Got result with action id %s", data.getStringExtra("action_id"))
			viewModel.setRunInProgress(false)
			viewModel.updateQueue(data.getStringExtra("action_id")!!)
		}
	}

	fun updateProgressText() {
		binding.progress.text = getString(R.string.in_progress_subtitle,
			viewModel.run.value!!.action_id_list.size - viewModel.pendingActionIdList.value!!.size,
			viewModel.run.value!!.action_id_list.size)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		outState.putString("running_action_id", viewModel.currentAction.value?.public_id)
		super.onSaveInstanceState(outState)
	}
}