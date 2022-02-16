package com.hover.runner.home

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
	fun navigateBack() {
		requireActivity().onBackPressed()
	}
}