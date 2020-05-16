package com.theapache64.mufy.commands

import com.theapache64.cyclone.core.base.BaseViewModel
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import com.theapache64.mufy.core.GifGenerator
import com.theapache64.mufy.core.HtmlGenerator
import com.theapache64.mufy.core.SortFilterManager
import com.theapache64.mufy.core.TrimManager
import java.io.File
import javax.inject.Inject

class MufyViewModel @Inject constructor(
    private val gifGenerator: GifGenerator,
    private val htmlGenerator: HtmlGenerator,
    private val trimManager: TrimManager,
    private val sortFilterManager: SortFilterManager
) : BaseViewModel<Mufy>() {

    private val _printer = MutableLiveData<String>()
    val printer: LiveData<String> = _printer

    companion object {
        const val RESULT_GIFS_GENERATED = 200
        const val RESULT_FAILED_TO_GENERATE_GIFS = 500
        const val RESULT_FAILED_LARGE_KEYWORD = 400


        const val NO_OF_GIF_MAXIMUM = -1
        const val START_GIF_BUFFER = 1.5
        const val END_GIF_BUFFER = 0.2
        const val MAX_KEYWORD_LENGTH = 21

        /**
         * A flag to debug
         */
        const val IS_NEED_MP4 = true

        val fontFile = File("${JarUtils.getJarDir()}assets/impact.ttf")
    }

    override suspend fun call(command: Mufy): Int {
        val inputFile = command.input
        val subTitleFile = File("${inputFile.absoluteFile.parent}/${inputFile.nameWithoutExtension}.srt")

        // Keyword validation
        for (keyword in command.keyword) {
            if (keyword.length > MAX_KEYWORD_LENGTH) {
                _printer.value = "$keyword crossed maximum keyword length $MAX_KEYWORD_LENGTH. Choose small keywords"
                return RESULT_FAILED_LARGE_KEYWORD
            }
        }

        // Subtitle validation
        if (!subTitleFile.exists()) {
            // subtitle missing
            _printer.value = "Subtitle file missing. Expected file : ${subTitleFile.absolutePath}"
            return RESULT_FAILED_TO_GENERATE_GIFS
        }

        _printer.value = "Subtitle found : ${subTitleFile.name}"
        val keywordSubtitles = sortFilterManager.filterKeywordSubTitles(subTitleFile, command.keyword)

        // Match validation
        if (keywordSubtitles.isEmpty()) {
            _printer.value = "Given keywords (${command.keyword.toList()}) does not present in ${inputFile.name}"
            return RESULT_FAILED_TO_GENERATE_GIFS
        }

        for (ks in keywordSubtitles) {
            _printer.value = "Found ${ks.subTitles.size} gif(s) with keyword '${ks.keyword}'"

            // Ordering by line length
            val sortedSubtitles = sortFilterManager.sortAndSubList(ks, command) {

                // High demand
                _printer.value =
                    "Requested number of gifs '${command.numOfGifs}' is higher than available gifs '${ks.subTitles.size}'"
            }

            val trimPositions = trimManager.getTrimPositions(ks.keyword, sortedSubtitles)

            // Generating gifs
            gifGenerator.createGifs(
                ks.keyword,
                inputFile,
                trimPositions
            ) { gifDir: File, gifFilePaths: List<String> ->
                val htmlFile = htmlGenerator.createHtmlFileFor(gifDir, gifFilePaths)
                _printer.value = "Done!"
                _printer.value = "Check out -> file://${htmlFile.absolutePath}"
            }
        }

        return RESULT_GIFS_GENERATED
    }

}