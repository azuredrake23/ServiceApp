package com.example.serviceapp.ui.settings

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
import com.example.serviceapp.ui.utils.mappers.AccessMapper
import com.example.serviceapp.ui.utils.mappers.AppLanguageMapper
import com.example.serviceapp.utils.Constants
import com.google.android.material.snackbar.Snackbar


class SettingsFragment : PreferenceFragmentCompat() {

    private val accessPreference: ListPreference? by lazy {
        findPreference(resources.getString(R.string.key_access_header))
    }

    private val languagePreferenceChangeListener =
        Preference.OnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                val language = AppLanguageMapper.map(newValue)
                updateLanguages(language)
            }
            true
        }

    private val accessPreferenceChangeListener =
        Preference.OnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                val access = AccessMapper.map(newValue)
                updateAccess(access)
            }
            true
        }

    private fun updateLanguages(language: LocaleListCompat): Boolean {
        AppCompatDelegate.setApplicationLocales(language)
        return true
    }

    private fun updateAccess(access: Boolean) {
        if (access)
            openAccessWindow()
    }

    private fun openAccessWindow() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Access check")
        val dialogLayout = layoutInflater.inflate(R.layout.custom_access_dialog, null)
        val login = dialogLayout.findViewById<EditText>(R.id.textAccessEmail)
        val password = dialogLayout.findViewById<EditText>(R.id.textAccessPassword)

        builder.setOnCancelListener {
            accessPreference!!.setValueIndex(0)
        }
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { _, _ ->

            if (login.text.toString() == Constants.loginCheck && password.text.toString() == Constants.passwordCheck) {
                accessPreference!!.setValueIndex(1)
            } else accessPreference!!.setValueIndex(0)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            accessPreference!!.setValueIndex(0)
        }
        builder.show()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        setChangeListener()
    }

    private fun setChangeListener() {
        val languagePreference: Preference? =
            findPreference(resources.getString(R.string.key_language_header))
        languagePreference?.onPreferenceChangeListener = languagePreferenceChangeListener

        accessPreference?.onPreferenceChangeListener = accessPreferenceChangeListener

//        accessPreference?.onPreferenceChangeListener { et ->
//            et.inputType = InputType.TYPE_CLASS_PHONE
//            et.keyListener = DigitsKeyListener.getInstance("0123456789")
//            et.setSelection(et.length())
//        }
//
//        editTextPrefThreshold?.onPreferenceChangeListener =
//            Preference.OnPreferenceChangeListener { _, newValue ->
//                if (newValue.toString().isInt() && newValue.toString().toInt().toFloat() in ThresholdMin..ThresholdMax)
//                    true
//                else {
//                    showSnackbar(resources.getString(R.string.threshold_error))
//                    false
//                }
//            }
    }

    @SuppressLint("ResourceType")
    private fun showSnackbar(message: String) {
        val snackbar =
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).setAction(message, null)
        val sbView: View = snackbar.view
        sbView.setBackgroundColor(Color.parseColor(getString(R.color.soft_grey)))
        snackbar.show()
    }
}