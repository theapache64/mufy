package com.theapache64.mufy.utils

object StringUtils {
    private val specCharRegEx by lazy { "\\W+".toRegex() }

    fun filterWords(
        text: String
    ): Set<String> {

        return text.replace(specCharRegEx, " ")
            .split(" ")
            .map { it.trim().toLowerCase() }
            .filter { it.length > 1 }
            .toSet()
    }
}

