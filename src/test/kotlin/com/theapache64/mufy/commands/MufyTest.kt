package com.theapache64.mufy.commands

import com.winterbe.expekt.should
import org.junit.Before
import org.junit.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter


class MufyTest {

    private lateinit var mufyCmd: CommandLine
    private val mufy = Mufy(true)

    @Before
    fun setUp() {
        this.mufyCmd = CommandLine(mufy)
        this.mufyCmd.out = PrintWriter(StringWriter())
    }

    @Test
    fun `Generate gifs with word`() {
        val exitCode =
            mufyCmd.execute(
                "-i",
                "/home/theapache64/Documents/projects/mufy/lab/movie.mp4",
                "-n",
                "20",
                "-k",
                "What",
                "-k",
                "Hey"
            )
        println(mufy.keyword.toList())
        exitCode.should.equal(MufyViewModel.RESULT_GIFS_GENERATED)
    }

    @Test
    fun `Generate gifs with 2 words`() {
        val exitCode =
            mufyCmd.execute(
                "-i",
                "/home/theapache64/Documents/projects/mufy/lab/movie.mp4",
                "-n",
                "20",
                "-k",
                "Thank you"
            )
        println(mufy.keyword.toList())
        exitCode.should.equal(MufyViewModel.RESULT_GIFS_GENERATED)
    }
}