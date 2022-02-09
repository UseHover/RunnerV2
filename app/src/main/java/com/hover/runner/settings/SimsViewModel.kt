package com.hover.runner.settings

import android.app.Application
import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hover.runner.utils.Utils
import com.hover.sdk.api.Hover
import com.hover.sdk.sims.SimInfo
import kotlinx.coroutines.launch

open class SimsViewModel(private val application: Application, val repo: SimsRepo) : ViewModel() {

	val sims: MutableLiveData<List<SimInfo>> = MutableLiveData()
	private var simReceiver: BroadcastReceiver? = null

	init {
		loadSims()
	}

	private fun loadSims() {
		viewModelScope.launch {
			sims.postValue(repo.simDao.present)
		}

		simReceiver?.let {
			LocalBroadcastManager.getInstance(application)
				.registerReceiver(it, IntentFilter(Utils.getPackage(application).plus(".NEW_SIM_INFO_ACTION")))
		}

		Hover.updateSimInfo(application)
	}

}