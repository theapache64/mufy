package com.theapache64.mufy.core

import com.theapache64.mufy.commands.Mufy
import com.theapache64.mufy.models.KeywordSubtitles
import com.theapache64.mufy.utils.srtparser.SrtParser
import com.theapache64.mufy.utils.srtparser.Subtitle
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SortFilterManager @Inject constructor() {
    /**
     * To sort given subtitles in length order and sublist if num of gifs given
     */
    fun sortAndSubList(
        ks: KeywordSubtitles,
        command: Mufy,
        onHighDemand: () -> Unit
    ): List<Subtitle> {
        return ks.subTitles.sortedBy { it.text.length }.let { sortedSubTitles ->
            if (command.numOfGifs > 0) {
                // num of gifs available
                if (command.numOfGifs > sortedSubTitles.size) {
                    // user ordered more than available
                    onHighDemand()
                    sortedSubTitles
                } else {
                    sortedSubTitles.subList(0, command.numOfGifs)
                }
            } else {
                sortedSubTitles
            }
        }
    }

    /**
     * To get matched subtitles for given keyword
     */
    fun filterKeywordSubTitles(subTitleFile: File, keywords: Array<String>): List<KeywordSubtitles> {

        val subTitles = SrtParser().parse(subTitleFile).subtitles
        val keywordSubtitles = mutableListOf<KeywordSubtitles>()
        // searching for each keyword
        for (keyword in keywords) {


            val matchedSubTitles = mutableListOf<Subtitle>()

            for (subTitle in subTitles) {
                if (isMatch(subTitle, keyword)) {
                    matchedSubTitles.add(subTitle)
                }
            }

            if (matchedSubTitles.isNotEmpty()) {
                keywordSubtitles.add(KeywordSubtitles(keyword, matchedSubTitles))
            }
        }

        return keywordSubtitles
    }


    private fun isMatch(subTitle: Subtitle, _keyword: String): Boolean {
        val keyword = _keyword.toLowerCase()
        val text = subTitle.text.toLowerCase().replace("\n", " ")

        return text.matches("^(?:.+\\s)?($keyword)(?:\\s|\\W|.+)?\$".toRegex())
    }

}