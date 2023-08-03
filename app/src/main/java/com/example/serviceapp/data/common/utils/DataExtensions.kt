package com.example.shapel.data.common.utils

fun String.isInt(): Boolean {
    return when(toIntOrNull()) {
        null -> false
        else -> true
    }
}












