package com.hover.runner.action.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hover.runner.R
import com.hover.runner.action.adapters.VariableRecyclerAdapter
import com.hover.runner.action.listeners.ActionVariableEditListener
import com.hover.runner.action.models.Action
import com.hover.runner.action.models.ActionVariablesCache
import com.hover.runner.action.models.StreamlinedSteps
import com.hover.runner.action.viewmodel.ActionViewModel
import com.hover.runner.base.fragment.BaseFragment
import com.hover.runner.databinding.UncompletedVariableFragmentLayoutBinding
import com.hover.runner.utils.RunnerColor
import com.hover.runner.utils.TextViewUtils.Companion.underline
import com.hover.runner.utils.UIHelper
import com.hover.runner.utils.UIHelper.Companion.setLayoutManagerToLinear
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class UnCompletedVariablesFragment : BaseFragment(), ActionVariableEditListener {
	private var _binding: UncompletedVariableFragmentLayoutBinding? = null
	private val binding get() = _binding!!

	private val actionViewModel: ActionViewModel by sharedViewModel()

	private var timer = Timer()
	private var initialUncompletedSize: Int = 0

	private lateinit var toolBarText: TextView
	private lateinit var subtoolBarText: TextView
	private lateinit var descTitleText: TextView
	private lateinit var descContentText: TextView
	private lateinit var nextSaveText: TextView
	private lateinit var skipTextMember1: TextView
	private lateinit var skipTextMember2: TextView
	private lateinit var skipTextMember3: TextView
	private lateinit var variablesRecyclerView: RecyclerView


	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		UIHelper.changeStatusBarColor(requireActivity(), RunnerColor(requireContext()).RED)
		_binding = UncompletedVariableFragmentLayoutBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initViews()
		underlineSkipText()
		setToolBarClick()
		observePositionedAction()
		setupSaveContinue()
		setupSkipText()
	}

	private fun initViews() {
		toolBarText = binding.uVariableToolbarText
		subtoolBarText = binding.uVariableSubtoolText
		descTitleText = binding.uVariableStatusTitle
		descContentText = binding.uVariableStatusDesc
		nextSaveText = binding.nextSaveTextId
		skipTextMember1 = binding.skipIdMember1
		skipTextMember2 = binding.skipIdMember2
		skipTextMember3 = binding.skipIdMember3
		variablesRecyclerView = binding.actionVariablesRecyclerView
		variablesRecyclerView.setLayoutManagerToLinear()
	}

	private fun underlineSkipText() {
		skipTextMember2.underline(resources.getString(R.string.skip_text))
	}

	private fun setToolBarClick() {
		toolBarText.setOnClickListener { navigateBack() }
	}

	private fun observePositionedAction() {
		actionViewModel.actionsWithUCV_LiveData.observe(viewLifecycleOwner) {
			if (it.isEmpty()) navigateBack()
			else {
				if (initialUncompletedSize == 0) initialUncompletedSize = it.size
				val currentAction = it[0]
				setDetailsText(currentAction, it.size)
				setVariableListAdapter(currentAction)
			}
		}
	}

	private fun getDescriptionSuffix(actionListSize: Int): String {
		return resources.getString(if (actionListSize == 1) R.string.act_missing_info_singular else R.string.act_missing_info_pluarl)
	}

	private fun setDetailsText(currentAction: Action, listSize: Int) {
		toolBarText.text = currentAction.id
		subtoolBarText.text = currentAction.title
		descTitleText.text =
			String.format(Locale.ENGLISH, "%d %s", listSize, getDescriptionSuffix(listSize))
		descContentText.text = String.format(Locale.getDefault(), "%d actions left ", listSize)
		nextSaveText.underline(if (listSize == 1) getString(R.string.save_text) else getString(R.string.save_continue))
	}

	private fun setupSaveContinue() {
		nextSaveText.setOnClickListener {
			with(actionViewModel) {
				if (canCurrentActionSave()) {
					val action = getCurrentUCVAction()
					action.removeFromSkipped(requireContext())
					removeFromUCVList(action)
				}
				else {
					UIHelper.flashMessage(requireContext(),
					                      getString(R.string.uncompleted_variable_unfilled))
				}
			}
		}
	}

	private fun setupSkipText() {
		fun skip() {
			with(actionViewModel) {
				val action = getCurrentUCVAction()
				action.saveAsSkipped(requireContext())
				removeFromUCVList(action)
			}
		}
		skipTextMember1.setOnClickListener { skip() }
		skipTextMember2.setOnClickListener { skip() }
		skipTextMember3.setOnClickListener { skip() }
	}

	private fun setVariableListAdapter(action: Action) {
		try {
			val streamlinedSteps =
				StreamlinedSteps.get(action.rootCode, JSONArray(action.jsonArrayToString))
			val variables = ActionVariablesCache.get(requireContext(), action.id).actionMap
			val adapter = VariableRecyclerAdapter(action.id, streamlinedSteps, this, variables)
			variablesRecyclerView.adapter = adapter
		} catch (e: Exception) {
			UIHelper.flashMessage(requireContext(),
			                      requireActivity().currentFocus,
			                      resources.getString(R.string.bad_steps_config))
		}
	}


	override fun updateVariableCache(label: String, value: String) {
		timer.cancel()
		timer = Timer()
		val actionId = actionViewModel.getCurrentUCVAction().id
		val task = object : TimerTask() {
			override fun run() {
				ActionVariablesCache.save(actionId, label, value, requireContext())
			}
		}
		timer.schedule(task, ActionVariablesCache.THROTTLE)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}