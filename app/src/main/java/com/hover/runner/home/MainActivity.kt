package com.hover.runner.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hover.runner.R
import com.hover.runner.actionDetails.ActionDetailViewModel
import com.hover.runner.databinding.ActivityMainBinding
import com.hover.runner.login.activities.SplashScreenActivity
import com.hover.runner.settings.SettingsFragment
import com.hover.runner.utils.PermissionsUtil
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.UIHelper
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import com.hover.sdk.api.HoverParameters
import com.hover.sdk.permissions.PermissionActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private val viewModel: ActionDetailViewModel by viewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initHover()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		checkForPermissions()
		redirectIfRequired()
		setupNavigation()
	}

	private fun redirectIfRequired() {
		if (!isLoggedIn()) {
			startActivity(Intent(this, SplashScreenActivity::class.java))
			finish()
			return
		}
	}

	private fun initHover() {
		Timber.e("initializing hover. key: %s", SharedPrefUtils.getApiKey(this))
		Hover.initialize(this, SharedPrefUtils.getApiKey(this))
		Hover.setBranding("Runner by Hover", R.drawable.ic_runner_logo, this)
	}

	private fun checkForPermissions() {
		val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
			if (result.resultCode != Activity.RESULT_OK) {
				UIHelper.flashMessage(this, currentFocus, getString(R.string.permissions_not_granted))
			}
		}
		if (isLoggedIn() && !PermissionsUtil.hasPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE))) {
			resultLauncher.launch(Intent(this, PermissionActivity::class.java))
		}
	}

	private fun setupNavigation() {
		val navView = findViewById<BottomNavigationView>(R.id.nav_view)
		NavigationUI.setupWithNavController(navView, findNavController(R.id.nav_host_fragment))

		if (intent.extras != null && intent.extras!!.getString("navigate") != null) {
			findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_transactions)
		}
	}

	private fun isLoggedIn(): Boolean {
		with(SharedPrefUtils.getApiKey(this)) {
			Timber.i("API key is: $this")
			return this != null && this.length > 5
		}
	}

	public fun startHover(action: HoverAction) {
		Timber.e("Starting hover action %s", action.public_id)
		val builder = HoverParameters.Builder(this)
		builder.request(action.public_id)
		builder.setEnvironment(SettingsFragment.getCurrentEnv(this))
		builder.finalMsgDisplayTime(0)
		action.requiredParams.forEach { builder.extra(it, SharedPrefUtils.getVarValue(action.public_id, it, application)) }
		startActivityForResult(builder.buildIntent(), 0)
	}

	@Override
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		Timber.e("Got result")
		if (data?.getStringExtra("action_id") != null) {
			Timber.e("Got result with action id %s", data.getStringExtra("action_id"))
			updateQueueAndGetNext(data.getStringExtra("action_id")!!)
		}
	}

	private fun updateQueueAndGetNext(finishedActionId: String) {
		val actionIdList = SharedPrefUtils.getQueue(this)
		actionIdList?.remove(finishedActionId)
		SharedPrefUtils.saveQueue(actionIdList?.toList(), this)
		if (actionIdList != null && actionIdList.size > 0) {
			Timber.e("loading next: %s", actionIdList[0])
			viewModel.action.observe(this) {
				viewModel.action.removeObservers(this)
				startHover(it)
			}
			viewModel.loadAction(actionIdList[0])
		}
	}
}