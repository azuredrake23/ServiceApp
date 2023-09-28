package com.example.serviceapp.ui.view_models

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.R
import com.example.serviceapp.domain.settings.usecase.GetAppLanguageUseCase
import com.example.serviceapp.ui.common_fragments.models.ValidationState
import com.example.serviceapp.utils.DialogType
import com.example.serviceapp.utils.mappers.AppLanguageMapper
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

    private val _listDialogFieldsState =
        MutableStateFlow <MutableList<ValidationState>>(mutableListOf(ValidationState.Inactive))
    val listDialogFieldsState: StateFlow <List<ValidationState>> get() = _listDialogFieldsState

    private val _nameDialogFieldsState =
        MutableStateFlow<ValidationState>(ValidationState.Inactive)
    val nameDialogFieldsState: StateFlow<ValidationState> get() = _nameDialogFieldsState

    private val _emailDialogFieldsState =
        MutableStateFlow<ValidationState>(ValidationState.Inactive)
    val emailDialogFieldsState: StateFlow<ValidationState> get() = _emailDialogFieldsState

    private val _passwordDialogFieldsState =
        MutableStateFlow<ValidationState>(ValidationState.Inactive)
    val passwordDialogFieldsState: StateFlow<ValidationState> get() = _passwordDialogFieldsState

    private val _deleteDialogFieldsState =
        MutableStateFlow<ValidationState>(ValidationState.Inactive)
    val deleteDialogFieldsState: StateFlow<ValidationState> get() = _deleteDialogFieldsState

    fun getAppLanguage() {
        viewModelScope.launch {
            val language = AppLanguageMapper.map(getAppLanguageUseCase.execute())
            if (language != _appLanguage.value)
                _appLanguage.update { language }
        }
    }

    fun validateFields(validationList: List<CharSequence>) {
        if (validationList.isEmpty()) return
        viewModelScope.launch {
            validationList.forEach {
                _listDialogFieldsState.value[validationList.indexOf(it)] = validateSource(it)
            }
        }
    }

    fun validateEmailDialog(source: CharSequence) {
        if (source.isBlank() && _emailDialogFieldsState.value == ValidationState.Inactive) return
        viewModelScope.launch {
            _emailDialogFieldsState.value =
                validateSource(source)
        }
    }

    fun validatePasswordDialog(source: CharSequence) {
        if (source.isBlank() && _passwordDialogFieldsState.value == ValidationState.Inactive) return
        viewModelScope.launch {
            _passwordDialogFieldsState.value =
                validateSource(source)
        }
    }

    fun validateDeleteDialog(source: CharSequence) {
        if (source.isBlank() && _passwordDialogFieldsState.value == ValidationState.Inactive) return
        viewModelScope.launch {
            _passwordDialogFieldsState.value =
                validateSource(source)
        }
    }

    private fun validateSource(src: CharSequence): ValidationState {
        if (src.isNotBlank()) {
            return ValidationState.Success(src.toString())
        }
        return ValidationState.Error(R.string.enter_value_message)
    }

    private fun isAllInputFieldValidated(dialogType: DialogType): Boolean =
        when (dialogType) {
            DialogType.USERNAME -> {
                _nameDialogFieldsState.value is ValidationState.Success
            }

            DialogType.EMAIL -> {
                _emailDialogFieldsState.value is ValidationState.Success
            }

            DialogType.PASSWORD -> {
                _passwordDialogFieldsState.value is ValidationState.Success
            }

            DialogType.DELETE -> {
                _deleteDialogFieldsState.value is ValidationState.Success
            }
        }
}