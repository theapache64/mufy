package com.theapache64.mufy.commands

import dagger.Component

@Component
interface MufyComponent {
    fun inject(mufy: Mufy)
}