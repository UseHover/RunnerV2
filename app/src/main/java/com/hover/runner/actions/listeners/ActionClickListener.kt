package com.hover.runner.actions.listeners

import android.view.View

interface ActionClickListener {
    fun onActionItemClick(actionId: String, titleTextView: View)
}