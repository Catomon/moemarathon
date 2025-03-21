package com.github.catomon.moemarathon.utils

fun interface OnResult<T> {
    fun onResult(result: T?)
}
