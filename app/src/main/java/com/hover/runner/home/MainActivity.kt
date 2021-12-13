package com.hover.runner.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.hover.runner.R
import com.hover.runner.databinding.ActivityMainBinding
import com.hover.runner.login.activities.SplashScreenActivity
import com.hover.runner.utils.PermissionsUtil
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.UIHelper
import com.hover.sdk.api.Hover
import com.hover.sdk.permissions.PermissionActivity
import timber.log.Timber

class MainActivity : AbstractViewModelActivity() {

	private lateinit var binding: ActivityMainBinding
	val permission_acceptance_incomplete = "You did not allow all permissions"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initHover()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		checkForPermissions()
		redirectIfRequired()
	}

	override fun onResume() {
		super.onResume()
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
		Hover.initialize(this, SharedPrefUtils.getApiKey(this))
		Hover.setBranding("Runner by Hover", R.drawable.ic_runner_logo, this)
	}

	private fun checkForPermissions() {
		val resultLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode != Activity.RESULT_OK) {
					UIHelper.flashMessage(this, currentFocus, permission_acceptance_incomplete)
				}
			}
		if (isLoggedIn() && !PermissionsUtil.hasPermissions(this,
		                                                    arrayOf(Manifest.permission.READ_PHONE_STATE,
		                                                            Manifest.permission.CALL_PHONE))) {
			resultLauncher.launch(Intent(this, PermissionActivity::class.java))
		}
	}

	private fun isLoggedIn(): Boolean {
		with(SharedPrefUtils.getApiKey(this)) {
			Timber.i("API key is: $this")
			return this != null && this.length > 5
		}
	}

}