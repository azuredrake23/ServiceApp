package com.example.serviceapp.ui.view_models.firebase_view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.utils.SignInState
import com.example.serviceapp.utils.SignUpState
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.Nullable
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    @Nullable val firebaseUser: FirebaseUser?,
    val oneTapClient: SignInClient,
    val signInRequest: BeginSignInRequest
) : ViewModel() {

    private val _phoneNumber = MutableSharedFlow<String>()
    val phoneNumber: Flow<String> get() = _phoneNumber

    private val _OTP = MutableSharedFlow<String>(replay = 1)
    val OTP: Flow<String> get() = _OTP

    private val _resendToken = MutableSharedFlow<PhoneAuthProvider.ForceResendingToken>()
    val resendToken: Flow<PhoneAuthProvider.ForceResendingToken> get() = _resendToken

    private val _signInState = MutableStateFlow<SignInState>(SignInState.UnsignedIn)
    val signInState: StateFlow<SignInState> get() = _signInState

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.UnsignedUp)
    val signUpState: StateFlow<SignUpState> get() = _signUpState

    fun updatePhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            _phoneNumber.emit(phoneNumber)
        }
    }

    fun updateOTP(otp: String) {
        viewModelScope.launch {
            _OTP.emit(otp)
        }
    }

    fun updateResendToken(resendToken: PhoneAuthProvider.ForceResendingToken) {
        viewModelScope.launch {
            _resendToken.emit(resendToken)
        }
    }

    fun updateSignInState(signInState: SignInState) {
        viewModelScope.launch {
            _signInState.update { signInState }
        }
    }

    fun getSignInState(): SignInState = _signInState.value

    fun updateSignUpState(signUpState: SignUpState) {
        viewModelScope.launch {
            _signUpState.update { signUpState }
        }
    }

    fun getSignUpState(): SignUpState = _signUpState.value
}