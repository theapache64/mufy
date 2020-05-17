package com.theapache64.mufy.core


import com.theapache64.mufy.utils.srtparser.getResourceFile
import com.winterbe.expekt.should
import org.junit.Before
import org.junit.Test


internal class SortFilterManagerTest {

    private lateinit var sfm: SortFilterManager

    @Before
    fun setUp() {
        this.sfm = SortFilterManager()
    }

    @Test
    fun `Filter matched keywords`() {
        val srtFile = getResourceFile("movie.srt")
        val keywords = arrayOf("what", "god", "hey", "fgdfgdfgdf")
        val filtered = sfm.filterKeywordSubTitles(srtFile, keywords)

        filtered.size.should.equal(3)

        filtered[0].keyword.should.equal("what")
        filtered[0].subTitles.size.should.equal(112)

        filtered[1].keyword.should.equal("god")
        filtered[1].subTitles.size.should.equal(2)
    }

    @Test
    fun `Filter words`() {
        val input = "This is some sample text. And this is got something!! this-is-hyphen-text"
        val result = sfm.filterWords(input)
        result.toString().should.equal("[this, is, some, sample, text, and, got, something, hyphen]")
    }

}