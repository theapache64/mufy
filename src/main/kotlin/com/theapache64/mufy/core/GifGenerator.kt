package com.theapache64.mufy.core

import com.theapache64.mufy.commands.MufyViewModel
import com.theapache64.mufy.models.TrimPosition
import com.theapache64.mufy.utils.SimpleCommandExecutor
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GifGenerator @Inject constructor() {

    /**
     * To generate gifs for the given input file with given trim position.
     * The directory will be named according to keyword and input file name.
     */
    fun createGifs(
        keyword: String,
        inputFile: File,
        trimPositions: List<TrimPosition>,
        caption: String?,
        onGifGenerated: (gifDir: File, gifFilePaths: List<String>) -> Unit
    ) {

        val gifFilePaths = mutableListOf<String>()
        val gifDir = File("gifs/${keyword}_${inputFile.nameWithoutExtension}")
        gifDir.deleteRecursively()
        gifDir.mkdirs()


        for ((index, trimPos) in trimPositions.withIndex()) {
            val posIndex = index + 1
            println("Generating gif ${posIndex}/${trimPositions.size}...")

            val gifFilePathWithoutExt = "${gifDir.absolutePath}/${posIndex}_${keyword}"
            val gifFilePath = "$gifFilePathWithoutExt.gif"

            val tempMp4File = File("${keyword}_${posIndex}_${trimPositions.size}_${System.currentTimeMillis()}.mp4")
            val gifCaption = (caption ?: keyword).toUpperCase()
            val command = """
                ffmpeg -y -ss ${trimPos.fromInSeconds} -t ${trimPos.durationInSeconds} -i '${inputFile.absolutePath}' -vf \
                "scale=512:-2,
                drawtext=fontfile=${MufyViewModel.fontFile.absolutePath}:fontsize=50:fontcolor=white:x=(w-text_w)/2:y=(h-text_h-10):text='${gifCaption}':bordercolor=black:borderw=2" \
                -c:v libx264 -an "${tempMp4File.absolutePath}" && ffmpeg -y -i "${tempMp4File.absolutePath}" -filter_complex "[0:v] fps=12,scale=480:-2,split [a][b];[a] palettegen [p];[b][p] paletteuse" "$gifFilePath" && rm "${tempMp4File.absolutePath}" 
            """.trimIndent()

            SimpleCommandExecutor.executeCommand(
                command,
                isLivePrint = false,
                isSuppressError = true,
                isReturnAll = true
            )

            @Suppress("ConstantConditionIf")
            if (MufyViewModel.IS_NEED_MP4) {

                val mp4GenCommand =
                    "ffmpeg -y -ss ${trimPos.fromInSeconds} -t ${trimPos.durationInSeconds} -i '${inputFile.absolutePath}' '${gifFilePathWithoutExt}.mp4'"
                SimpleCommandExecutor.executeCommand(
                    mp4GenCommand,
                    isLivePrint = false,
                    isSuppressError = true,
                    isReturnAll = true
                )
            }

            gifFilePaths.add(gifFilePath)
        }

        onGifGenerated(gifDir, gifFilePaths)
    }
}