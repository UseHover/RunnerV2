package com.hover.runner.parser.repo

import android.content.Context
import com.hover.sdk.parsers.HoverParser

class ParserRepo(private val context: Context) {
    suspend fun getParsersByActionId(actionId: String?): List<HoverParser> {
        return HoverParser.loadUSSDForAction(actionId, context)
    }

    suspend fun getParser(id: Int): HoverParser? {
        return HoverParser.load(IntArray(id), context).first()
    }
}