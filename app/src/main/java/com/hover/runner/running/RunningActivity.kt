package com.hover.runner.running

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import com.hover.runner.R
import com.hover.runner.databinding.ActivityTestRunningBinding
import com.hover.runner.settings.SettingsFragment
import com.hover.runner.testRuns.RUN_ID
import com.hover.runner.testRuns.TestRun
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.UIHelper
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.HoverParameters
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

const val TAG = "hover:runningtest"

class RunningActivity : AppCompatActivity() {
	private lateinit var binding: ActivityTestRunningBinding

	private val viewModel: RunningViewModel by viewModel()
	private lateinit var wakeLock: PowerManager.WakeLock

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		wakeUp()

		UIHelper.changeStatusBarColor(this, getColor(this, R.color.runnerPrimary))
		binding = ActivityTestRunningBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)

		startObservers(savedInstanceState)
		load();
	}

	private fun wakeUp() {
		this.window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
		val isReady = if (Build.VERSION.SDK_INT >= 20) powerManager.isInteractive else powerManager.isScreenOn
//		if (!isReady) {
			disableKeyguard()
			wakeLock = powerManager.newWakeLock(
				PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, TAG)
			wakeLock.acquire(10*60*1000L /*10 minutes*/)
//		}
	}

	private fun disableKeyguard() {
		val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
		val keyguardLock = keyguardManager!!.newKeyguardLock(TAG)
		if (Build.VERSION.SDK_INT >= 26)
			keyguardManager.requestDismissKeyguard(this, null)
		else
			keyguardLock.disableKeyguard()
	}

//	private fun onWake(): KeyguardManager.KeyguardDismissCallback? {
//
//	}

	private fun startObservers(savedInstanceState: Bundle?) {
		viewModel.currentAction.observe(this) {
			if (it != null && !viewModel.isTestRunning() &&
				(savedInstanceState?.getString("running_action_id") == null ||
						savedInstanceState.getString("running_action_id") != it.public_id))
				startHover(it)
		}

		viewModel.run.observe(this) {
			it?.let { updateProgressText(it) }
		}
	}

	private fun load() {
		if (intent.hasExtra(RUN_ID)) {
			Timber.e("loaded run id %d", intent.getLongExtra(RUN_ID, -1))
			if (viewModel.run.value == null)
				viewModel.loadRun(intent.getLongExtra(RUN_ID, -1L))
		} else {
			Timber.e("Problem: no run id")
			UIHelper.flashMessage(this, getString(R.string.notify_run_error))
			finish();
		}
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

	fun updateProgressText(run: TestRun) {
		binding.progress.text = getString(R.string.in_progress_subtitle,
			run.action_id_list.size - run.pending_action_id_list.size, run.action_id_list.size)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		outState.putString("running_action_id", viewModel.currentAction.value?.public_id)
		super.onSaveInstanceState(outState)
	}

	override fun onDestroy() {
		wakeLock.release()
		super.onDestroy()
	}
}