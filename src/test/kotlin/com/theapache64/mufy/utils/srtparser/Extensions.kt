package com.theapache64.mufy.utils.srtparser

import java.io.File

fun getResourceFile(fileName: String): File {
    return File(Thread.currentThread().contextClassLoader.getResource(fileName)!!.file)
}