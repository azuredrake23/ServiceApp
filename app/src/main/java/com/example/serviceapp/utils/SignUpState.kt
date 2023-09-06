package com.example.serviceapp.utils

sealed class SignUpState {
    object SignedUp: SignUpState()
    object UnsignedUp: SignUpState()
}