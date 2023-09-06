package com.example.serviceapp.data.common.utils

import android.content.Context
import androidx.annotation.StringRes

class ResourceManager(private val context: Context) {

    fun getString(@StringRes resId: Int): String = context.getString(resId)

    fun getString(@StringRes resId: Int, message: String): String =
        context.getString(resId, message)

}