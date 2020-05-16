package com.theapache64.mufy.core


import com.theapache64.mufy.utils.srtparser.getResourceFile
import com.winterbe.expekt.should
import org.junit.Before
import org.junit.Test


internal class SortFilterManagerTest {

    private lateinit var sortFilterManager: SortFilterManager

    @Before
    fun setUp() {
        this.sortFilterManager = SortFilterManager()
    }

    @Test
    fun `Filter matched keywords`() {
        val srtFile = getResourceFile("movie.srt")
        val keywords = arrayOf("what", "god", "hey", "fgdfgdfgdf")
        val filtered = sortFilterManager.filterKeywordSubTitles(srtFile, keywords)

        filtered.size.should.equal(3)

        filtered[0].keyword.should.equal("what")
        filtered[0].subTitles.size.should.equal(112)

        filtered[1].keyword.should.equal("god")
        filtered[1].subTitles.size.should.equal(2)
    }


}