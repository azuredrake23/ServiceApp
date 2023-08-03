package com.example.serviceapp.ui.main

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shapel.data.domain.settings.usecase.GetAppLanguageUseCase
import com.example.serviceapp.ui.utils.mappers.AppLanguageMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAppLanguageUseCase: GetAppLanguageUseCase
) : ViewModel() {

    private val _appLanguage = MutableStateFlow(AppCompatDelegate.getApplicationLocales())
    val appLanguage: StateFlow<LocaleListCompat> get() = _appLanguage

    fun getAppLanguage() {
        viewModelScope.launch {
            val language = AppLanguageMapper.map(getAppLanguageUseCase.execute())
            if (language != _appLanguage.value)
                _appLanguage.update { language }
        }
    }
}