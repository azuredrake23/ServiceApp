package com.example.serviceapp.data.models

sealed class DownloadDialogState {
    object Show: DownloadDialogState()
    object Dismiss: DownloadDialogState()
    object Inactive: DownloadDialogState()
}