package com.example.serviceapp.utils.mappers

import androidx.core.os.LocaleListCompat
import com.example.serviceapp.data.mappers.Mapper

object AppLanguageMapper : Mapper<String, LocaleListCompat> {

    override fun map(data: String): LocaleListCompat =
        when (data) {
            Language.ENGLISH.name -> LocaleListCompat.forLanguageTags("en-US")
            Language.RUSSIAN.name -> LocaleListCompat.forLanguageTags("ru-RU")
            else -> LocaleListCompat.getDefault()
        }

}

enum class Language {
    ENGLISH,
    RUSSIAN
}
