package com.example.serviceapp.data.common.utils

import android.content.Context
import android.widget.Toast

fun String.isInt(): Boolean {
    return when(toIntOrNull()) {
        null -> false
        else -> true
    }
}
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}












