package com.example.serviceapp.data.models

sealed class SignUpState {
    object SignedUp: SignUpState()
    object UnsignedUp: SignUpState()
}