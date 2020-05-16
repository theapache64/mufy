package com.theapache64.mufy.commands

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface MufyComponent {
    fun inject(mufy: Mufy)
}