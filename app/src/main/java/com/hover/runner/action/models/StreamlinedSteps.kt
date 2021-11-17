package com.hover.runner.action.models

import com.google.gson.Gson
import com.hover.runner.BuildConfig
import org.json.JSONArray
import java.util.*


private data class RawStepsModel(
    var value: String,
    var description: String,
    var isIs_param: Boolean
)

data class StreamlinedSteps(
    val fullUSSDCodeStep: String,
    val stepVariableLabel: List<String>,
    val stepsVariableDesc: List<String>
) {
    companion object {

        fun get(_rootCode: String, jsonArray: JSONArray): StreamlinedSteps {
            var rootCode = _rootCode
            val gson = Gson()
            val rawStepsModel: Array<RawStepsModel> =
                gson.fromJson(jsonArray.toString(), Array<RawStepsModel>::class.java)
            if (rootCode.contains("null")) rootCode = "STK#"
            val stepSuffix = StringBuilder()
            val stepsVariableLabels = ArrayList<String>()
            val stepsVariableDesc = ArrayList<String>()
            for (rawStep in rawStepsModel) {
                stepSuffix.append("*").append(rawStep.value)
                if (BuildConfig.FLAVOR.contains("pro")) {
                    if (rawStep.isIs_param || rawStep.value.equals("pin")) {
                        stepsVariableLabels.add(rawStep.value)
                        stepsVariableDesc.add(rawStep.description)
                    }
                } else {
                    if (rawStep.isIs_param) {
                        stepsVariableLabels.add(rawStep.value)
                        stepsVariableDesc.add(rawStep.description)
                    }
                }
            }
            //Taking substring of root code to remove the last #. E.g *737# to become *737
            val readableStep = (if (rootCode.isNotEmpty()) rootCode.substring(
                0,
                rootCode.length - 1
            ) else "") + stepSuffix + "#"
            return StreamlinedSteps(readableStep, stepsVariableLabels, stepsVariableDesc)
        }
    }
}