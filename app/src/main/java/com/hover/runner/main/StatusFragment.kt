package com.hover.runner.main

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
	fun navigateBack() {
		requireActivity().onBackPressed()
	}
}