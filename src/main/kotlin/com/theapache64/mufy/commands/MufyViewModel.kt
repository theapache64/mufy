package com.theapache64.mufy.commands

import com.theapache64.cyclone.core.base.BaseViewModel
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import java.io.File
import javax.inject.Inject

class MufyViewModel @Inject constructor() : BaseViewModel<Mufy>() {

    private val _printer = MutableLiveData<String>()
    val printer: LiveData<String> = _printer

    companion object {
        const val RESULT_GIFS_GENERATED = 200
        const val RESULT_FAILED_TO_GENERATE_GIFS = 500
        const val RESULT_FAILED_NO_SUBTITLE = 400
        const val NO_OF_GIF_MAXIMUM = -1
    }

    override suspend fun call(command: Mufy): Int {
        val inputFile = command.input
        val subTitleFile = File("${inputFile.parent}/${inputFile.nameWithoutExtension}.srt")
        if (subTitleFile.exists()) {
            // all good
            _printer.value = "Subtitle found : ${subTitleFile.name}"


        } else {
            // subtitle missing
            _printer.value = "Subtitle file missing. Expected file : ${subTitleFile.absolutePath}"
            return RESULT_FAILED_NO_SUBTITLE
        }
        return RESULT_FAILED_TO_GENERATE_GIFS;
    }

}