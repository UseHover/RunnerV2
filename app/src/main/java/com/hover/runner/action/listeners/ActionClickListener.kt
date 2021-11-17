package com.hover.runner.action.listeners

import android.view.View

interface ActionClickListener {
    fun onActionItemClick(actionId: String, titleTextView: View)
}