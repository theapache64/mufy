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
    fun createHtmlFileFor(gifDir: File, gifFilePaths: List<String>): File {

        val imageCols = gifFilePaths.map { gifFilePath ->
            val tempFile = File(gifFilePath)
            @Suppress("ConstantConditionIf")
            if (MufyViewModel.IS_NEED_MP4) {
                return@map """
                    <div class="col-md-4">
                        <a  href="${tempFile.parent}/${tempFile.nameWithoutExtension}.mp4" target="_blank">
                            <img class="photo_item" src="$gifFilePath"/>
                        </a>
                    </div>
            """.trimIndent()
            } else {
                """
                    <div class="col-md-4">
                        <img class="photo_item" src="$gifFilePath"/>
                    </div>
                """.trimIndent()
            }
        }

        val chunked = imageCols.chunked(3).map { img3 ->
            """
                <div class="row">
                    ${img3.joinToString(" ")}
                 </div>
            """.trimIndent()
        }


        val html = """
            
<html>
<head>
    <title>Mufy Output</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- CSS only -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">

    <style>
        img.photo_item {
            border: transparent 0px solid ;
            border-radius: 5px;
            margin-top: 20px;
            width: 100%;
        }
    </style>
</head>

<body>
<div class="container">
    <h1>Mufy Output <small>${gifDir.name} / ${gifFilePaths.size} gif(s)</small></h1>
    ${chunked.joinToString(" ")}
</div>

</body>

</html>
        """.trimIndent()

        val file = File("${gifDir.absolutePath}/index.html")
        file.writeText(html)
        return file
    }

}