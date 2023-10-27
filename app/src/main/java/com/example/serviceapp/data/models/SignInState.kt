package com.example.serviceapp.data.models

sealed class SignInState {
    object Google: SignInState()
    object PhoneNumber: SignInState()
    object UnsignedIn: SignInState()
}