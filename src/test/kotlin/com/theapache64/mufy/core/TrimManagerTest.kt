package com.theapache64.mufy.core

import com.theapache64.mufy.utils.srtparser.SrtParser
import com.theapache64.mufy.utils.srtparser.getResourceFile
import com.winterbe.expekt.should
import org.junit.Before
import org.junit.Test

internal class TrimManagerTest {

    private lateinit var trimManager: TrimManager

    @Before
    fun setUp() {
        this.trimManager = TrimManager()
    }

    @Test
    fun `Trim position calculation`() {
        val srtFile = getResourceFile("movie.srt")
        val subTitles = SrtParser().parse(srtFile).subtitles.filter {
            it.index == 98L
        }
        val trimPositions = trimManager.getTrimPositions("what", subTitles)
        trimPositions.size.should.equal(1)
        trimPositions[0].durationInSeconds.should.above(1.toDouble())
    }
}