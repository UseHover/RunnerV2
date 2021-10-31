package com.hover.runner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hover.runner.databinding.ActivityMainBinding
import com.hover.runner.login.activities.SplashScreenActivity
import com.hover.runner.utils.PermissionsUtil
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.UIHelper
import com.hover.sdk.api.Hover
import com.hover.sdk.permissions.PermissionActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val permission_acceptance_incomplete = "You did not allow all permissions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        initHover()
        checkForPermissions()
        setContentView(binding.root)
        setupNavigation()
        redirectIfRequired()
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

        if (!PermissionsUtil.hasPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
            )
        ) {
            resultLauncher.launch(Intent(this, PermissionActivity::class.java))
        }
    }


    private fun setupNavigation() {
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navView, navController)

        if (intent.extras != null) {
            if (intent.extras!!.getString("navigate") != null) {
                navController.navigate(R.id.navigation_transactions)
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        with(SharedPrefUtils.getApiKey(this)) {
            return this != null && this.length > 5
        }
    }

}