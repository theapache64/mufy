package com.theapache64.mufy.commands

import com.theapache64.cyclone.core.base.BaseViewModel
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import com.theapache64.mufy.core.GifGenerator
import com.theapache64.mufy.core.HtmlGenerator
import com.theapache64.mufy.core.SortFilterManager
import com.theapache64.mufy.core.TrimManager
import com.theapache64.mufy.utils.JarUtils
import com.theapache64.mufy.utils.srtparser.SrtParser
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject

class MufyViewModel @Inject constructor(
    private val gifGenerator: GifGenerator,
    private val htmlGenerator: HtmlGenerator,
    private val trimManager: TrimManager,
    private val sfm: SortFilterManager,
    private val srtParser: SrtParser
) : BaseViewModel<Mufy>() {

    private val _printer = MutableLiveData<String>()
    val printer: LiveData<String> = _printer

    private val _inlinePrinter = MutableLiveData<String>()
    val inlinePrinter: LiveData<String> = _inlinePrinter

    companion object {
        const val RESULT_GIFS_GENERATED = 200
        const val RESULT_FAILED_TO_GENERATE_GIFS = 500
        const val RESULT_FAILED_LARGE_KEYWORD = 400
        const val RESULT_FAILED_LARGE_CAPTION = 405


        const val NO_OF_GIF_MAXIMUM = -1
        const val START_GIF_BUFFER = 1.5
        const val END_GIF_BUFFER = 0.2
        const val MAX_KEYWORD_LENGTH = 21
        const val MAX_CAPTION_LENGTH = MAX_KEYWORD_LENGTH

        /**
         * A flag to debug
         */
        const val IS_NEED_MP4 = true

        val fontFile = File("${JarUtils.getJarDir()}assets/impact.ttf")
    }

    override suspend fun call(command: Mufy): Int {
        val inputFile = command.input
        val subTitleFile = File("${inputFile.absoluteFile.parent}/${inputFile.nameWithoutExtension}.srt")

        // Input file validation
        if (!inputFile.exists()) {
            _printer.value = "File doesn't exist '${inputFile.absolutePath}'"
            return RESULT_FAILED_TO_GENERATE_GIFS
        }

        // Subtitle validation
        if (!subTitleFile.exists()) {
            // subtitle missing
            _printer.value = "Subtitle file missing. Expected file : ${subTitleFile.absolutePath}"
            return RESULT_FAILED_TO_GENERATE_GIFS
        }

        // Flag check
        if (command.keywords.isNotEmpty() && command.isKeywordFromSubtitle) {
            _printer.value = "Option '-k' and '-kfs' can't be worked together."
            return RESULT_FAILED_TO_GENERATE_GIFS
        }

        // Keyword empty check
        if (command.keywords.isEmpty() && command.isKeywordFromSubtitle) {

            _printer.value = "Creating keywords from subtitle..."

            val subTitles = srtParser.parse(subTitleFile).subtitles
            val keywords = mutableSetOf<String>()
            // Parsing
            for (subTitle in subTitles) {
                val lineKeywords = sfm.filterWords(subTitle.text)
                keywords.addAll(lineKeywords)
            }

            _printer.value = "Found ${keywords.size} word(s) from '${subTitleFile.name}'"
            command.keywords = keywords.toTypedArray()
        }

        // Keywords check
        if (command.keywords.isEmpty() && !command.isKeywordFromSubtitle) {
            _printer.value = "Keywords can't be empty"
            return RESULT_FAILED_TO_GENERATE_GIFS
        }


        // Keyword validation
        for (keyword in command.keywords) {
            if (keyword.length > MAX_KEYWORD_LENGTH) {
                _printer.value = "'$keyword' crossed maximum keyword length $MAX_KEYWORD_LENGTH. Choose small keywords"
                return RESULT_FAILED_LARGE_KEYWORD
            }
        }

        // Caption validation
        if (command.caption != null && command.caption!!.length > MAX_CAPTION_LENGTH) {
            _printer.value =
                "'${command.caption}' crossed maximum caption length $MAX_CAPTION_LENGTH."
            return RESULT_FAILED_LARGE_CAPTION
        }

        _printer.value = "Subtitle found : ${subTitleFile.name}"
        val keywordSubtitles = sfm.filterKeywordSubTitles(subTitleFile, command.keywords)

        // Match validation
        if (keywordSubtitles.isEmpty()) {
            _printer.value = "Given keywords ${command.keywords.toList()} does not present in ${inputFile.name}"
            return RESULT_FAILED_TO_GENERATE_GIFS
        }

        for (ks in keywordSubtitles) {
            _printer.value = "Found ${ks.subTitles.size} gif(s) with keyword '${ks.keyword}'"

            // Ordering by line length
            val sortedSubtitles = sfm.sortAndSubList(ks, command) {

                // High demand
                _printer.value =
                    "Requested number of gifs '${command.numOfGifs}' is higher than available gifs '${ks.subTitles.size}'"
            }

            val trimPositions = trimManager.getTrimPositions(ks.keyword, sortedSubtitles)

            // Generating gifs
            gifGenerator.createGifs(
                ks.keyword,
                inputFile,
                trimPositions,
                command.caption,
                { current: Int, total: Int ->
                    _inlinePrinter.value = "Generating gif ${current}/${total}..."
                }
            ) { gifDir: File, gifFilePaths: List<String> ->
                // Gif generation completed
                val htmlFile = htmlGenerator.createHtmlFileFor(gifDir, gifFilePaths)
                _printer.value = "Done!"
                val filePath = URLEncoder.encode(htmlFile.absolutePath, "UTF-8")
                    .replace("%2F", "/")
                    .replace("+", "%20")

                _printer.value = "Check out -> \"file://$filePath\""
            }
        }

        return RESULT_GIFS_GENERATED
    }


}