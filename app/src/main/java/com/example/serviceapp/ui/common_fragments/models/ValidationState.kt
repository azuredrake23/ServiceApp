package com.example.serviceapp.ui.common_fragments.models

sealed class ValidationState {
    data class Error(val messageStringId: Int) : ValidationState()
    data class Success(val text: String) : ValidationState()
    object Inactive : ValidationState()
}
