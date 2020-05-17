package com.theapache64.mufy.models

data class TrimPosition(
    val fromInSeconds: Double,
    val toInSeconds: Double
) {
    val durationInSeconds = toInSeconds - fromInSeconds

    override fun toString(): String {
        return "TrimPosition(fromInSeconds=$fromInSeconds, toInSeconds=$toInSeconds, durationInSeconds=$durationInSeconds)"
    }
}