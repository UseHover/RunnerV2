package com.hover.runner.customViews.detailsTopLayout

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.hover.runner.action.navigation.ActionNavigationInterface
import com.hover.runner.databinding.DetailsTopLayoutBinding
import com.hover.runner.utils.TextViewUtils.Companion.underline

enum class DetailScreenType {
	ACTION, TRANSACTION
}

class RunnerTopDetailsView(context: Context, attributeSet: AttributeSet) :
	TopDetailsContentChooser(context, attributeSet) {
	private var binding: DetailsTopLayoutBinding =
		DetailsTopLayoutBinding.inflate(LayoutInflater.from(context), this, true)

	private var titleText: TextView = binding.actionDetailsToolbarText
	private var subtitleText: TextView = binding.actionDetailsSubtoolText
	private var descTitle: TextView = binding.actionDetailsStatusTitle
	private var descContent: TextView = binding.actionDetailsStatusDesc
	private var descLink: TextView = binding.actionDetailsStatusLink
	private var topLayout: LinearLayout = binding.actionDetailsTopLayoutId


	private fun setLayout(status: String) {
		topLayout.setBackgroundColor(getLayoutBackground(status))
	}

	fun setTitle(content: String, status: String) {
		titleText.text = content
		titleText.setTextColor(getTitleTextColor(status))
		titleText.setCompoundDrawablesWithIntrinsicBounds(getTitleTextCompoundDrawable(status),
		                                                  0,
		                                                  0,
		                                                  0)
	}

	fun setSubTitle(content: String, status: String) {
		subtitleText.text = content
		subtitleText.setTextColor(getSubTitleTextColor(status))
	}

	fun setup(status: String, detailsScreenType: DetailScreenType, activity: Activity) {
		setLayout(status)
		setDescription(status, detailsScreenType)
		setDescriptionVisibility(status)
		setViewClicks(status, activity)
	}

	private fun setDescription(status: String, detailsScreenType: DetailScreenType) {
		descTitle.setText(getDescTitle(status, detailsScreenType))
		descTitle.setCompoundDrawablesWithIntrinsicBounds(getDescCompoundDrawable(status), 0, 0, 0)
		descTitle.compoundDrawablePadding = 32

		descContent.setText(getDescContent(status, detailsScreenType))
		descLink.underline(resources.getString(getDescLinkLabel(status)))
	}

	private fun setDescriptionVisibility(status: String) {
		descTitle.visibility = getDescVisibility(status)
		descContent.visibility = getDescVisibility(status)
	}

	private fun setViewClicks(status: String, activity: Activity) {
		descLink.setOnClickListener {
			val navInterface = activity as ActionNavigationInterface
			navInterface.navWebView(resources.getString(getWebTitle(status)),
			                        resources.getString(getLink()))
		}

		titleText.setOnClickListener { activity.onBackPressed() }

	}

}