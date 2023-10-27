package com.example.serviceapp.ui.common_fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.serviceapp.R
import com.example.serviceapp.utils.mappers.AppLanguageMapper
import com.example.serviceapp.utils.Constants
import com.google.android.material.snackbar.Snackbar


class SettingsFragment : PreferenceFragmentCompat() {

    private val languagePreferenceChangeListener =
        Preference.OnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                val language = AppLanguageMapper.map(newValue)
                updateLanguages(language)
            }
            true
        }

    private fun updateLanguages(language: LocaleListCompat): Boolean {
        AppCompatDelegate.setApplicationLocales(language)
        return true
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_fragment, rootKey)
        setChangeListener()
    }

    private fun setChangeListener() {
        val languagePreference: Preference? =
            findPreference(resources.getString(R.string.key_language_header))
        languagePreference?.onPreferenceChangeListener = languagePreferenceChangeListener
    }
}