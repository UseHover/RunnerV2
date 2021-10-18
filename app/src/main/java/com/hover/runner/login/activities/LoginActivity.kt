package com.hover.runner.login.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hover.runner.R
import com.hover.runner.databinding.LoginActivity2Binding
import com.hover.runner.login.viewmodel.LoginViewModel
import com.hover.runner.utils.Resource
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.Utils
import com.hover.runner.webview.WebViewActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var errorEmailText: TextView
    private lateinit var errorPasswordText:TextView
    private lateinit var emailLabel:TextView
    private lateinit var passwordLabel:TextView
    private lateinit var signInButton: Button
    private lateinit var forgotPassword : TextView

    private val validateViewModel : LoginViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =  LoginActivity2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews(binding)
        setupForgotPassword()
        setupEmailEdit()
        setupPasswordEdit()
        observeValidation()
        setupSignInButton()
        setTransition()
    }

    private fun initViews(binding: LoginActivity2Binding) {
        forgotPassword = binding.forgotPasswordText
        signInButton = binding.signinButton
        emailEdit = binding.emailEditId
        passwordEdit = binding.passwordEditId
        errorEmailText = binding.errorTextEmail
        errorPasswordText = binding.errorTextPassword
        emailLabel = binding.emailLabelId
        passwordLabel = binding.passwordLabelId
    }

    private fun setupForgotPassword() {
        UIHelper.setTextUnderline(forgotPassword, getString(R.string.forgot_password))
        forgotPassword.setOnClickListener { v: View? ->
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("title", getString(R.string.forgot_password))
            intent.putExtra("url", getString(R.string.url_forgot_password))
            startActivity(intent)
        }
    }

    private fun setupEmailEdit() {
        emailEdit.setOnClickListener { v: View? ->
            undoErrorView(
                emailEdit,
                errorEmailText,
                emailLabel
            )
        }
    }
    private fun setupPasswordEdit() {
        passwordEdit.setOnClickListener { v: View? ->
            undoErrorView(
                passwordEdit,
                errorPasswordText,
                passwordLabel
            )
        }
        passwordEdit.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                callSignIn()
            }
            false
        }
    }


    private fun observeValidation() {
        validateViewModel.validationLiveData.observe(this, {
            when(it) {
                is Resource.Success -> {
                    SharedPrefUtils.saveEmail(emailEdit.text.toString(), this)
                    sendDataToPreviousActivity()
                }

                else -> {
                    it.data?.let { error ->
                        signInButton.isClickable = true
                        handleDataError(error)
                    }
                }
            }
        })
    }


    private fun setupSignInButton() {
        signInButton.setOnClickListener { callSignIn() }
    }
    private fun callSignIn() {
        val email = emailEdit.text.toString()
        val password = passwordEdit.getText().toString()
        validateViewModel.validate(email, password)
    }
    private fun setTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val fade = Fade()
            fade.excludeTarget(android.R.id.statusBarBackground, true)
            fade.excludeTarget(android.R.id.navigationBarBackground, true)
            window.enterTransition = fade
            window.exitTransition = fade
        }
    }

    private fun sendDataToPreviousActivity() {
        val returnIntent = Intent()
        val result = arrayOf(emailEdit.text.toString(), passwordEdit.getText().toString())
        returnIntent.putExtra("login_data", result)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    private fun handleDataError(error: Int) {
        when(error) {
            R.string.INVALID_EMAIL -> showInvalidEmail(error)
            R.string.INVALID_PASSWORD -> showInvalidPassword(error)
            R.string.NO_NETWORK -> UIHelper.flashMessage(this, currentFocus, getString(R.string.NO_NETWORK))
        }
    }

    private fun showInvalidPassword(stringRes: Int) {
        //Just to make sure email error get's cleared
        if (errorEmailText.visibility == View.VISIBLE) undoErrorView(
            emailEdit,
            errorEmailText,
            emailLabel
        )
        //set password error afterwards
        setErrorView(passwordEdit, errorPasswordText, passwordLabel)
        errorPasswordText.setText(stringRes)
    }
    private fun showInvalidEmail(stringRes: Int) {
        //Just to make sure password error gets cleared
        if (errorPasswordText.visibility == View.VISIBLE) undoErrorView(
            passwordEdit,
            errorPasswordText,
            passwordLabel
        )
        //Set email error afterwards
        setErrorView(emailEdit, errorEmailText, emailLabel)
        errorEmailText.setText(stringRes)
    }

    private fun setErrorView(editText: EditText, errorText: TextView, label: TextView) {
        editText.isActivated = true
        editText.setTextColor(resources.getColor(R.color.colorRed))
        errorText.visibility = View.VISIBLE
        label.setTextColor(resources.getColor(R.color.colorRed))
    }

    private fun undoErrorView(editText: EditText, errorText: TextView, label: TextView) {
        editText.isActivated = false
        editText.setTextColor(resources.getColor(R.color.colorHoverWhite))
        errorText.visibility = View.GONE
        label.setTextColor(resources.getColor(R.color.colorHoverWhite))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
    }
}