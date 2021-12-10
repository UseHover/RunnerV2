package com.hover.runner.parser.repo

import android.content.Context
import com.hover.sdk.parsers.HoverParser

class ParserRepo(private val context: Context) {
	suspend fun getParsersByActionId(actionId: String?): List<HoverParser> {
		return HoverParser.loadUSSDForAction(actionId, context)
	}

	suspend fun getParser(actionId: String, parserId: Int): HoverParser? {
		return HoverParser.loadUSSDForAction(actionId, context).find { it.serverId == parserId }
	}
}