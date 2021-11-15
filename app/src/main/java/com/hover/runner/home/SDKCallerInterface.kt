package com.hover.runner.home

interface SDKCallerInterface {
    fun runChainedActions()
    fun runAction(actionId: String)
}