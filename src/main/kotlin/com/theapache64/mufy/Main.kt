package com.theapache64.mufy

import com.theapache64.mufy.commands.Mufy
import picocli.CommandLine
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val exitCode = CommandLine(Mufy(false)).execute(*args)
    exitProcess(exitCode)
}