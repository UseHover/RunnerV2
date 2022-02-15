package com.hover.runner.testRuns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import com.hover.runner.R
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.FragmentFillVariablesBinding
import com.hover.runner.databinding.FragmentRunInProgressBinding
import com.hover.runner.utils.UIHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InProgressFragment : BaseFragment() {
	private var _binding: FragmentRunInProgressBinding? = null
	private val binding get() = _binding!!

	private val runViewModel: RunViewModel by sharedViewModel()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		UIHelper.changeStatusBarColor(requireActivity(), getColor(requireContext(), R.color.runnerPrimary))
		_binding = FragmentRunInProgressBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}