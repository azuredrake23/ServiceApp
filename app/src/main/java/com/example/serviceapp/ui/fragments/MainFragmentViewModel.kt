package com.example.serviceapp.ui.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
) : ViewModel() {

    private val _accessState = MutableStateFlow(false)
    var accessState: StateFlow<Boolean> = _accessState

    fun setAccessState(state: Boolean) {
        viewModelScope.launch {
            _accessState.emit(state)
        }
    }
}