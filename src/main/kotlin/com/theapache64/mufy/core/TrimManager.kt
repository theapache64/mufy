package com.theapache64.mufy.core

import com.theapache64.mufy.commands.MufyViewModel
import com.theapache64.mufy.models.TrimPosition
import com.theapache64.mufy.utils.srtparser.Subtitle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrimManager @Inject constructor() {

    /**
     * To get GIF trim positions from give subtitle and keyword
     */
    fun getTrimPositions(keyword: String, subTitles: List<Subtitle>): List<TrimPosition> {

        val trimPositions = mutableListOf<TrimPosition>()

        for (subTitle in subTitles) {

            // Calculating trim position
            val duration = subTitle.end.minus(subTitle.begin)
            val durInSec = duration.toSeconds()
            val charCount = subTitle.text.length
            val timeForChar = durInSec / charCount
            val totalTimeNeededForKeywordInMs = keyword.length * timeForChar
            val firstIndex = subTitle.text.indexOf(keyword, 0, true)
            val seekMs = firstIndex * totalTimeNeededForKeywordInMs

            val stWithoutBuffer = subTitle.begin.toSeconds() + seekMs
            val startTime = stWithoutBuffer - MufyViewModel.START_GIF_BUFFER
            val endTime = stWithoutBuffer + totalTimeNeededForKeywordInMs + MufyViewModel.END_GIF_BUFFER

            trimPositions.add(TrimPosition(startTime, endTime))
        }

        return trimPositions
    }

}