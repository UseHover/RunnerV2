package com.hover.runner.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hover.runner.R
import com.hover.runner.databinding.ActivityMainBinding
import com.hover.runner.login.activities.SplashScreenActivity
import com.hover.runner.testRuns.RUN_ID
import com.hover.runner.utils.PermissionsUtil
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.UIHelper
import com.hover.sdk.api.Hover
import com.hover.sdk.permissions.PermissionActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding

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

	@Override
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_run_details, bundleOf(RUN_ID to requestCode.toLong()))
	}
}