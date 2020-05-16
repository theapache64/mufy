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
        const val GIF_BUFFER = 1.5

        val fontFile = File("assets/impact.ttf")
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
                    return RESULT_GIFS_GENERATED
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

    /**
     * To generate gifs for the given input file with given trim position.
     * The directory will be named according to keyword and input file name.
     */
    private fun createGifs(keyword: String, inputFile: File, trimPositions: List<TrimPosition>) {

        val gifFilePaths = mutableListOf<String>()
        val gifDir = File("gifs/${keyword}_${inputFile.nameWithoutExtension}")

        if (!gifDir.exists()) {
            gifDir.mkdirs()
        }

        for ((index, trimPos) in trimPositions.withIndex()) {
            val posIndex = index + 1
            println("Generating gif ${posIndex}/${trimPositions.size}...")

            val gifFilePath = "${gifDir.absolutePath}/${posIndex}_${keyword}.gif"

            val tempMp4File = File("${keyword}_${posIndex}_${trimPositions.size}_${System.currentTimeMillis()}.mp4")
            val command = """
                ffmpeg -y -ss ${trimPos.fromInSeconds} -t ${trimPos.durationInSeconds} -i '${inputFile.absolutePath}' -vf \
                "scale=512:-1,
                drawtext=fontfile=${fontFile.absolutePath}:fontsize=50:fontcolor=white:x=(w-text_w)/2:y=(h-text_h-10):text='${keyword.toUpperCase()}':bordercolor=black:borderw=2" \
                -c:v libx264 -an "${tempMp4File.absolutePath}" && ffmpeg -y -i "${tempMp4File.absolutePath}" -filter_complex "[0:v] fps=12,scale=480:-1,split [a][b];[a] palettegen [p];[b][p] paletteuse" "$gifFilePath" && rm "${tempMp4File.absolutePath}" 
            """.trimIndent()

            SimpleCommandExecutor.executeCommand(
                command,
                isLivePrint = false,
                isSuppressError = true,
                isReturnAll = true
            )
            gifFilePaths.add(gifFilePath)
        }

        createHtmlFileFor(gifDir, gifFilePaths)
    }

    /**
     * To create an HTML index page for the given gif files
     */
    private fun createHtmlFileFor(gifDir: File, gifFileNames: List<String>) {

        val imgSrcs = gifFileNames.map { gifFilePath ->
            """<img src="$gifFilePath"/> </br> """
        }

        val html = """
            <html>
            <body>
                $imgSrcs
            </body>
            </html>
        """.trimIndent()

        File("${gifDir.absolutePath}/index.html").writeText(html)
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
            val endTime = stWithoutBuffer + totalTimeNeededForKeywordInMs
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
                    if (matchedSubTitles.size < 10) {
                        matchedSubTitles.add(subTitle)
                    }
                }
            }

            if (matchedSubTitles.isNotEmpty()) {
                keywordSubtitles.add(KeywordSubtitles(keyword, matchedSubTitles))
            }
        }

        return keywordSubtitles
    }

}