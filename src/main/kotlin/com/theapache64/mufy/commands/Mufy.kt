package com.theapache64.mufy.commands

import com.theapache64.cyclone.core.base.BaseCommand
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@CommandLine.Command(
    name = "mufy",
    version = ["v1.0.0-alpha03"],
    mixinStandardHelpOptions = true
)
@Singleton
class Mufy constructor(isFromTest: Boolean = false) : BaseCommand<Int>(isFromTest) {


    @CommandLine.Option(
        names = ["-i", "--input"],
        required = true,
        description = ["Input file to be processed"]
    )
    lateinit var input: File

    @CommandLine.Option(
        names = ["-k", "--keyword"],
        description = ["Keywords (comma-separated)"]
    )
    var keywords: Array<String> = arrayOf()

    @CommandLine.Option(
        names = ["-n", "--number-of-gifs"],
        description = ["Number of gifs to be generated."],
        defaultValue = MufyViewModel.NO_OF_GIF_MAXIMUM.toString()
    )
    var numOfGifs: Int = MufyViewModel.NO_OF_GIF_MAXIMUM

    @CommandLine.Option(
        names = ["-c", "--caption"],
        description = ["Caption to be displayed on the GIF. By default, passed keyword will be displayed."]
    )
    var caption: String? = null

    @CommandLine.Option(
        names = ["-kfs", "--keywords-from-subtitle"],
        description = ["If enabled, keywords will be collected from subtitles (all words)"]
    )
    var isKeywordFromSubtitle: Boolean = false

    @Inject
    lateinit var mufyViewModel: MufyViewModel

    init {
        DaggerMufyComponent.builder().build().inject(this)
    }

    override fun call(): Int = runBlocking {

        mufyViewModel.printer.observe {
            println(it)
        }

        mufyViewModel.call(this@Mufy)
    }

}