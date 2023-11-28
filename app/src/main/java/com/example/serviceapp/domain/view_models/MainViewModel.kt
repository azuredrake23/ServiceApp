package com.example.serviceapp.domain.view_models

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.R
import com.example.serviceapp.data.models.DownloadDialogState
import com.example.serviceapp.domain.settings.usecase.GetAppLanguageUseCase
import com.example.serviceapp.data.models.ValidationState
import com.example.serviceapp.utils.DownloadDialog
import com.example.serviceapp.utils.isInRange
import com.example.serviceapp.utils.mappers.AppLanguageMapper
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _downloadDialogState =
        MutableStateFlow<DownloadDialogState>(DownloadDialogState.Inactive)
    val downloadDialogState: StateFlow<DownloadDialogState> get() = _downloadDialogState

    private val _popupValue = MutableSharedFlow<String>()
    val popupValue: SharedFlow<String> get() = _popupValue

    private val _warningDialogMessage = MutableSharedFlow<String>()
    val warningDialogMessage: SharedFlow<String> get() = _warningDialogMessage

    private val _navigateFragmentValue = MutableSharedFlow<Int>()
    val navigateFragmentValue: SharedFlow<Int> get() = _navigateFragmentValue

    fun getAppLanguage() {
        viewModelScope.launch {
            val language = AppLanguageMapper.map(getAppLanguageUseCase.execute())
            if (language != _appLanguage.value)
                _appLanguage.update { language }
        }
    }

    fun navigate(fragment: Int) {
        viewModelScope.launch {
            _navigateFragmentValue.emit(fragment)
        }
    }

    fun popupMessage(message: String) {
        viewModelScope.launch {
            _popupValue.emit(message)
        }
    }

    fun warningDialog(message: String){
        viewModelScope.launch {
            _warningDialogMessage.emit(message)
        }
    }

    fun updateDialogState(downloadDialogState: DownloadDialogState){
        viewModelScope.launch {
            _downloadDialogState.emit(downloadDialogState)
        }
    }

    fun checkDownloadDialogState(activity: FragmentActivity, dialogState: DownloadDialogState){
        when (dialogState) {
            DownloadDialogState.Show -> DownloadDialog.showDownloadDialog(activity)
            DownloadDialogState.Dismiss -> DownloadDialog.cancelDownloadDialog()
            DownloadDialogState.Inactive -> {}
        }
    }


}