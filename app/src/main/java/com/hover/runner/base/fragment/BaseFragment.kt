package com.hover.runner.base.fragment

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
	fun navigateBack() {
		requireActivity().onBackPressed()
	}
}