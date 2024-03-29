package com.hover.runner.settings

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.hover.runner.R
import com.hover.runner.databinding.SettingsFragmentBinding
import com.hover.runner.login.activities.LoginActivity
import com.hover.runner.utils.SharedPrefUtils
import com.hover.runner.utils.SharedPrefUtils.DELAY
import com.hover.runner.utils.SharedPrefUtils.ENV
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.Utils
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.*

class SettingsFragment : Fragment(), Hover.DownloadListener {
	private val PROD_ENV: Int = 0
	private val DEBUG_ENV: Int = 1
	private val TEST_ENV: Int = 2

	private var _binding: SettingsFragmentBinding? = null
	private val binding get() = _binding!!

	private var isRefreshButtonIdle = false
	private val simsViewModel: SimsViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = SettingsFragmentBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observeSimNames()
		setupEnvRadio()
		linkify(binding.contactSupport)
		setupDelayEntry()

		binding.refreshButton.setOnClickListener { confirmRefresh() }
		binding.email.text = SharedPrefUtils.getEmail(requireContext())
		binding.packageName.text = Utils.getPackage(requireContext())
		binding.apiKey.text = SharedPrefUtils.getApiKey(requireContext())
		binding.signOutButton.setOnClickListener { showSignOutDialog() }
	}

	private fun setupDelayEntry() {
		binding.delayInput.setText(SharedPrefUtils.getDelay(requireContext()).toString())
		binding.delayInput.addTextChangedListener(delayWatcher)
	}

	private val delayWatcher: TextWatcher = object : TextWatcher {
		override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
		override fun afterTextChanged(editable: Editable) {}
		override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
			if (charSequence.isNotEmpty())
				SharedPrefUtils.setDelay(charSequence.toString().toInt(), requireContext())
		}
	}

	private fun observeSimNames() {
		simsViewModel.sims.observe(viewLifecycleOwner) { sims ->
			if (!sims.isNullOrEmpty())
				binding.sim1Content.text = sims[0].operatorName + "(" + sims[0].osReportedHni + ")"
			if (!sims.isNullOrEmpty() && sims.size > 1)
				binding.sim2Content.text = sims[1].operatorName + "(" + sims[1].osReportedHni + ")"
		}
	}

	private fun setupEnvRadio() {
		when (getCurrentEnv(requireContext())) {
			PROD_ENV -> binding.envRadioGroup.check(R.id.mode_normal)
			DEBUG_ENV -> binding.envRadioGroup.check(R.id.mode_debug)
			else -> binding.envRadioGroup.check(R.id.mode_noSim)
		}
		binding.envRadioGroup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
			updateEnv(checkedId)
		}
	}

	private fun confirmRefresh() {
		try {
			val builder = AlertDialog.Builder(requireContext())
			builder.setTitle("App refresh")
			builder.setMessage("Refreshing your app data will delete all cached entries. Are you sure you want continue?")
			builder.setPositiveButton("Refresh") { dialog: DialogInterface?, _: Int ->
				dialog?.dismiss()
				dialog?.cancel()
				refreshActions()
			}
			builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
				dialog.dismiss()
				dialog.cancel()
			}
			builder.show()
		} catch (e: Exception) {
			Timber.e(e)
		}
	}

	private fun linkify(contactSupport: TextView) { //TODO: Method name is weird, needs to be changed to something better. Please go ahead to change if you've got a better idea.
		val sp = HtmlCompat.fromHtml(getString(R.string.contactUs),
		                             HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS)
		contactSupport.text = sp
		contactSupport.movementMethod = LinkMovementMethod.getInstance()
	}

	private fun refreshActions() {
		if (!isRefreshButtonIdle) {
			isRefreshButtonIdle = true
			Hover.updateActionConfigs(this, requireContext())
			UIHelper.flashMessage(requireContext(),
			                      resources.getString(R.string.app_data_refreshed))
		}
	}

	private fun showSignOutDialog() {
		val builder = AlertDialog.Builder(requireContext())
		builder.setTitle("Sign out")
		builder.setMessage("Are you sure you want to sign out of Runner app?")
		builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int -> signOut() }
		builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
			dialog.dismiss()
			dialog.cancel()
		}
		builder.show()
	}


	private fun setEnv(mode: Int) = SharedPrefUtils.saveInt(ENV, mode, requireContext())
	private fun updateEnv(pos: Int) {
		when (pos) {
			R.id.mode_normal -> setEnv(PROD_ENV)
			R.id.mode_debug -> setEnv(DEBUG_ENV)
			R.id.mode_noSim -> setEnv(TEST_ENV)
		}
	}

	private fun signOut() {
		SharedPrefUtils.clearData(requireContext())
		startActivity(Intent(requireActivity(), LoginActivity::class.java))
	}

	override fun onError(reason: String?) {
		isRefreshButtonIdle = false
		UIHelper.flashMessage(requireContext(), reason)
	}

	override fun onSuccess(p0: ArrayList<HoverAction>?) {
		isRefreshButtonIdle = false
	}

	companion object {
		fun getCurrentEnv(context: Context): Int = SharedPrefUtils.getSavedInt(ENV, context)
		fun envToString(env: Int): String {
			var string = ""
			string = when (env) {
				0 -> "Normal"
				1 -> "Debug"
				else -> "No-SIM"
			}
			return string
		}
	}
}