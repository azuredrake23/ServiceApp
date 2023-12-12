package com.example.serviceapp.data.models

sealed class SignInState {
    object Google: SignInState()
    object PhoneNumber: SignInState()
    object GoogleAndPhoneNumber: SignInState()
    object UnsignedIn: SignInState()
}