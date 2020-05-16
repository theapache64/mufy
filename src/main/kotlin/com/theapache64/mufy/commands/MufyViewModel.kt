package com.theapache64.mufy.commands

import com.theapache64.cyclone.core.base.BaseViewModel
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import com.theapache64.mufy.models.KeywordSubtitles
import com.theapache64.mufy.models.TrimPosition
import com.theapache64.mufy.utils.SimpleCommandExecutor
import com.theapache64.mufy.utils.srtparser.SrtParser
import com.theapache64.mufy.utils.srtparser.Subtitle
import java.io.File
import javax.inject.Inject

class MufyViewModel @Inject constructor() : BaseViewModel<Mufy>() {

    private val _printer = MutableLiveData<String>()
    val printer: LiveData<String> = _printer

    companion object {
        const val RESULT_GIFS_GENERATED = 200
        const val RESULT_FAILED_TO_GENERATE_GIFS = 500
        const val NO_OF_GIF_MAXIMUM = -1
        const val GIF_BUFFER = 1
    }

    override suspend fun call(command: Mufy): Int {
        val inputFile = command.input
        val subTitleFile = File("${inputFile.parent}/${inputFile.nameWithoutExtension}.srt")
        if (subTitleFile.exists()) {
            // all good
            _printer.value = "Subtitle found : ${subTitleFile.name}"
            val keywordSubtitles = filterKeywordSubTitles(subTitleFile, command.keyword)
            if (keywordSubtitles.isNotEmpty()) {

                for (ks in keywordSubtitles) {
                    _printer.value = "Found ${ks.subTitles.size} instance(s) of '${ks.keyword}'"

                    // Ordering by line length
                    val sortedSubtitles = sortAndSubList(ks, command)
                    val trimPositions = getTrimPositions(ks.keyword, sortedSubtitles)
                    createGifs(ks.keyword, inputFile, trimPositions)
                }

            } else {
                // no match found found
                _printer.value = "Given keywords (${command.keyword.toList()}) does not present in ${inputFile.name}"
            }

        } else {
            // subtitle missing
            _printer.value = "Subtitle file missing. Expected file : ${subTitleFile.absolutePath}"
        }

        return RESULT_FAILED_TO_GENERATE_GIFS;
    }

    private fun createGifs(keyword: String, inputFile: File, trimPositions: List<TrimPosition>) {

        for ((index, trimPos) in trimPositions.withIndex()) {
            val posIndex = index + 1
            println("Generating gif ${posIndex}/${trimPositions.size}...")

            val command = """
                ffmpeg -y -ss ${trimPos.fromInSeconds} -t ${trimPos.durationInSeconds} -i '${inputFile.absolutePath}' -vf \
                "scale=512:-1,
                drawtext=fontfile=impact.ttf:fontsize=50:fontcolor=white:x=(w-text_w)/2:y=(h-text_h-10):text='${keyword.toUpperCase()}':bordercolor=black:borderw=2" \
                -c:v libx264 -an cut.mp4 && ffmpeg -y -i cut.mp4 -vf "scale=256:-1" "${posIndex}_${keyword}_${inputFile.nameWithoutExtension}.gif" && rm cut.mp4
            """.trimIndent()

            SimpleCommandExecutor.executeCommand(command)
            break
        }
    }

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
            val startTime = stWithoutBuffer - GIF_BUFFER
            val endTime = stWithoutBuffer + totalTimeNeededForKeywordInMs + GIF_BUFFER
            trimPositions.add(TrimPosition(startTime, endTime))
        }

        return trimPositions
    }

    /**
     * To sort given subtitles in length order and sublist if num of gifs given
     */
    private fun sortAndSubList(
        ks: KeywordSubtitles,
        command: Mufy
    ): List<Subtitle> {
        return ks.subTitles.sortedBy { it.text.length }.let { sortedSubTitles ->
            if (command.numOfGifs > 0) {
                // num of gifs available
                if (sortedSubTitles.size > command.numOfGifs) {
                    // user ordered more than available
                    _printer.value =
                        "Requested number of gifs '${command.numOfGifs}' is higher than available gifs '${sortedSubTitles.size}'"
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
        for (_keyword in keywords) {

            val keyword = _keyword.toLowerCase()

            val matchedSubTitles = mutableListOf<Subtitle>()

            for (subTitle in subTitles) {
                if (subTitle.text.toLowerCase().contains(keyword)) {
                    matchedSubTitles.add(subTitle)
                }
            }

            if (matchedSubTitles.isNotEmpty()) {
                keywordSubtitles.add(KeywordSubtitles(keyword, matchedSubTitles))
            }
        }
        return keywordSubtitles
    }

}