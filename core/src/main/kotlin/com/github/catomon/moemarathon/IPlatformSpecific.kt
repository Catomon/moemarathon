package com.github.catomon.moemarathon

var platformSpecific: IPlatformSpecific? = null

interface IPlatformSpecific {

    fun desktopOpenMapsFolder() {

    }
}
