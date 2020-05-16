package com.theapache64.mufy.utils.srtparser

import com.winterbe.expekt.should
import org.junit.Test


internal class SrtParserTest {

    @Test
    fun `Parse good file`() {
        val srtFile = getResourceFile("movie.srt")
        val parsed = SrtParser().parse(srtFile)
        parsed.subtitles.size.should.above(100)
    }


}