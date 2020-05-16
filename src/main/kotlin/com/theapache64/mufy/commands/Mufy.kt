package com.theapache64.mufy.commands

import com.theapache64.cyclone.core.base.BaseCommand
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@CommandLine.Command(
    name = "mufy",
    version = ["v1.0.0-alpha01"],
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
        required = true,
        description = ["Keywords (comma-separated)"]
    )
    lateinit var keyword: Array<String>

    @CommandLine.Option(
        names = ["-n", "--number-of-gifs"],
        defaultValue = MufyViewModel.NO_OF_GIF_MAXIMUM.toString()
    )
    var numOfGifs: Int = MufyViewModel.NO_OF_GIF_MAXIMUM

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