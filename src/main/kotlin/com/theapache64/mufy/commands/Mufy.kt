package com.theapache64.mufy.commands

import com.theapache64.cyclone.core.base.BaseCommand
import picocli.CommandLine
import javax.inject.Inject
import javax.inject.Singleton

@CommandLine.Command(
    name = "mufy",
    version = ["v1.0.0-alpha01"],
    mixinStandardHelpOptions = true
)
@Singleton
class Mufy constructor(isFromTest: Boolean = false) : BaseCommand<Int>(isFromTest) {

    companion object {
        const val GIFS_GENERATED = 200
    }

    override fun call(): Int {
        TODO("Not yet implemented")
    }

}