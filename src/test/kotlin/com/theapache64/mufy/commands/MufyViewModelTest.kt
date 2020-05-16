package com.theapache64.mufy.commands

import com.theapache64.mufy.utils.srtparser.SrtParser
import com.theapache64.mufy.utils.srtparser.Subtitle
import com.theapache64.mufy.utils.srtparser.getResourceFile
import com.winterbe.expekt.should
import org.junit.Before
import org.junit.Test


internal class MufyViewModelTest {

    private lateinit var mufyViewModel: MufyViewModel


    @Before
    fun setUp() {
        this.mufyViewModel = MufyViewModel()
    }

    @Test
    fun `Filter matched keywords`() {
        val srtFile = getResourceFile("movie.srt")
        val keywords = arrayOf("what", "god", "hey", "fgdfgdfgdf")
        val filtered = mufyViewModel.filterKeywordSubTitles(srtFile, keywords)

        filtered.size.should.equal(3)

        filtered[0].keyword.should.equal("what")
        filtered[0].subTitles.size.should.equal(112)

        filtered[1].keyword.should.equal("god")
        filtered[1].subTitles.size.should.equal(2)
    }

    @Test
    fun `Trim position calculation`() {
        val srtFile = getResourceFile("movie.srt")
        val subTitles = SrtParser().parse(srtFile).subtitles.filter {
            it.index == 98L
        }
        val trimPositions = mufyViewModel.getTrimPositions("what", subTitles)
        trimPositions.size.should.equal(1)
        trimPositions[0].durationInSeconds.should.above(2.toDouble())
    }
}