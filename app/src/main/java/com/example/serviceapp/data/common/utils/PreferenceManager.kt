package com.example.serviceapp.data.common.utils

import android.content.Context
import androidx.preference.PreferenceManager

class PreferenceManager(context: Context) {

    private val preferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun get(key: String, defaultValue: String): String =
        preferences.getString(key, null) ?: defaultValue

    fun get(key: String, defaultValue: Boolean): Boolean =
        preferences.getBoolean(key, defaultValue)

    fun get(key: String, defaultValue: Float): Float =
        preferences.getFloat(key, defaultValue)

    fun get(key: String, defaultValue: Int): Int =
        preferences.getInt(key, defaultValue)

    fun get(key: String, defaultValue: Long): Long =
        preferences.getLong(key, defaultValue)

    fun <T> put(key: String, data: T) {
        preferences.edit().apply {
            when (data) {
                is String -> putString(key, data)
                is Boolean -> putBoolean(key, data)
                is Float -> putFloat(key, data)
                is Double -> putFloat(key, data.toFloat())
                is Int -> putInt(key, data)
                is Long -> putLong(key, data)
            }
        }.apply()
    }

}