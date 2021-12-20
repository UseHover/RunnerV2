package com.hover.runner.parser

import android.content.Context
import com.hover.sdk.parsers.HoverParser

class ParserRepo(private val context: Context) {
	fun getParsersByActionId(actionId: String?): List<HoverParser> {
		return HoverParser.loadUSSDForAction(actionId, context)
	}

	fun getParser(parserId: Int): HoverParser? {
		return HoverParser.load(arrayOf(parserId).toIntArray(), context)[0]
	}
}