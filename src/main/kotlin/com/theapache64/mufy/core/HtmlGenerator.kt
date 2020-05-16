package com.theapache64.mufy.core

import com.theapache64.mufy.commands.MufyViewModel
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HtmlGenerator @Inject constructor() {

    /**
     * To create an HTML index page for the given gif files
     */
    fun createHtmlFileFor(gifDir: File, gifFilePaths: List<String>) {

        val imgSrcs = gifFilePaths.map { gifFilePath ->
            val tempFile = File(gifFilePath)
            @Suppress("ConstantConditionIf")
            if (MufyViewModel.IS_NEED_MP4) {
                return@map """
                <a href="${tempFile.parent}/${tempFile.nameWithoutExtension}.mp4" target="_blank">
                    <img src="$gifFilePath"/>
                </a>
                <img src=""/>
            """.trimIndent()
            } else {
                """<img src="$gifFilePath"/> </br> """
            }
        }

        val html = """
            <html>
            <body>
                ${imgSrcs.joinToString("</br>\n")}
            </body>
            </html>
        """.trimIndent()

        File("${gifDir.absolutePath}/index.html").writeText(html)
    }

}