package com.hover.runner.actions.models

data class StreamlinedStepsModel(
    val fullUSSDCodeStep: String,
    val stepVariableLabel: List<String>,
    val stepsVariableDesc: List<String>
)