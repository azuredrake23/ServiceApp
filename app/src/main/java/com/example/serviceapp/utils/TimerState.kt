package com.example.serviceapp.utils

import java.util.Timer

sealed class TimerState {
    data class Processing(val leftTime: Int? = null): TimerState()
    object Stopped: TimerState()
}