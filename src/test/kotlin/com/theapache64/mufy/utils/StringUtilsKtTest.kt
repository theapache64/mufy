package com.theapache64.mufy.utils

import com.winterbe.expekt.should
import org.junit.Test


internal class StringUtilsKtTest {
    @Test
    fun `Filter words`() {
        val input = "This is some sample text. And this is got something!! this-is-hyphen-text"
        val result = StringUtils.filterWords(input)
        result.toString().should.equal("[this, is, some, sample, text, and, got, something, hyphen]")
    }
}