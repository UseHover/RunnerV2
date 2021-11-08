package com.hover.runner.login.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.hover.runner.home.MainActivity
import com.hover.runner.R
import com.hover.runner.databinding.SplashScreenLayoutBinding
import com.hover.runner.login.viewmodel.LoginViewModel
import com.hover.runner.utils.Resource
import com.hover.runner.utils.UIHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var loginProgress: ProgressBar
    private lateinit var imageView: ImageView
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = SplashScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews(binding)
        setupTransitions()
        delayNavToLoginActivity()
        observeLogin()
    }

    private fun initViews(binding: SplashScreenLayoutBinding) {
        imageView = binding.hoverBg1
        loginProgress = binding.loginProgress
    }

    private fun setupTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val fade = Fade()
            fade.excludeTarget(android.R.id.statusBarBackground, true)
            fade.excludeTarget(android.R.id.navigationBarBackground, true)
            window.enterTransition = fade
            window.exitTransition = fade
        }
    }

    private fun delayNavToLoginActivity() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1500)
            navToLoginActivity()
        }
    }

    private fun navToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loginProgress.visibility = View.GONE
            val imageView2 = findViewById<ImageView>(R.id.iv1)
            val sharedElement1 = Pair<View, String>(
                imageView,
                Objects.requireNonNull(ViewCompat.getTransitionName(imageView))
            )
            val sharedElement2 = Pair<View, String>(
                imageView2,
                Objects.requireNonNull(ViewCompat.getTransitionName(imageView2))
            )
            val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedElement1,
                sharedElement2
            )
            loginProcessLauncher.launch(intent, activityOptionsCompat)
        } else loginProcessLauncher.launch(intent)
    }

    private fun observeLogin() {
        loginViewModel.loginLiveData.observe(this, {
            when (it) {
                is Resource.Loading -> {
                    loginProgress.isIndeterminate = true
                    loginProgress.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
                is Resource.ErrorWithRes -> {
                    it.data?.let { stringRes ->
                        UIHelper.flashMessage(
                            this,
                            currentFocus,
                            getString(stringRes)
                        )
                    }
                    navToLoginActivity()
                }

            }
        })

    }

    private val loginProcessLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Array<String> = result.data!!.getStringArrayExtra("login_data")!!
                if (data.size == 2) {
                    val email = data[0];
                    val password = data[1]
                    loginViewModel.doLogin(email, password)
                } else delayNavToLoginActivity()
            }
        }


}