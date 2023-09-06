package com.example.serviceapp.utils

sealed class SignInState {
    object Google: SignInState()
    object PhoneNumber: SignInState()
    object UnsignedIn: SignInState()
}