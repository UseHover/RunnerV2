package com.hover.runner.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.hover.runner.actions.ActionsViewModel
import com.hover.runner.parser.ParserViewModel
import com.hover.runner.settings.SimsViewModel
import com.hover.runner.transaction.viewmodel.TransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class SDKCallerActivity : AppCompatActivity() {
	private val actionsViewModel: ActionsViewModel by viewModel()
	private val transactionViewModel: TransactionViewModel by viewModel()
	private val simsViewModel: SimsViewModel by viewModel()
	private val parserViewModel: ParserViewModel by viewModel()

	private lateinit var chainedActionLauncher: ActivityResultLauncher<Intent>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
//		initChainedLauncher()
	}

	fun runAction(intent: Intent) { startActivity(intent) }

	private fun initChainedLauncher() {
		chainedActionLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//				runChainedActions()
			}
	}
}