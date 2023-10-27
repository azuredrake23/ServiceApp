package com.example.serviceapp.domain.view_models.firebase_view_models

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.R
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.common.utils.ResourceManager
import com.example.serviceapp.data.models.DownloadDialogState
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.models.SignUpState
import com.example.serviceapp.domain.view_models.MainViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val mainViewModel: MainViewModel,
    val firebaseAuth: FirebaseAuth,
    private val oneTapClient: SignInClient,
    val resourceManager: ResourceManager,
    private val firebaseRealtimeDatabaseUserReference: DatabaseReference,
    private val signInRequest: BeginSignInRequest
) : ViewModel() {

    private val _phoneNumber = MutableSharedFlow<String>()
    val phoneNumber: SharedFlow<String> get() = _phoneNumber

    private val _OTP = MutableSharedFlow<String>(replay = 1)
    val OTP: SharedFlow<String> get() = _OTP

    private val _resendToken = MutableSharedFlow<ForceResendingToken>()
    val resendToken: SharedFlow<ForceResendingToken> get() = _resendToken

    private val _signInState = MutableStateFlow<SignInState>(SignInState.UnsignedIn)
    val signInState: StateFlow<SignInState> get() = _signInState

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.UnsignedUp)
    val signUpState: StateFlow<SignUpState> get() = _signUpState

    private val _isWrongOTP = MutableStateFlow(false)
    val isWrongOTP: StateFlow<Boolean> get() = _isWrongOTP

    private val _progressBarVisibility = MutableStateFlow(View.VISIBLE)
    val progressBarVisibility: StateFlow<Int> get() = _progressBarVisibility

    private val callbackPhoneNumber =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                viewModelScope.launch {
                    mainViewModel.popupMessage(e.message.toString())
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                mainViewModel.updateDialogState(DownloadDialogState.Dismiss)
//                downloadDialog.cancelDownloadDialog()
                viewModelScope.launch {
                    mainViewModel.navigate(
                        fragment = R.id.otp_fragment, bundle = bundleOf(
                            "verificationId" to verificationId,
                            "token" to token,
                            "phoneNumber" to phoneNumber
                        )
                    )
                }
            }
        }

    private val callbackOTP = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            mainViewModel.popupMessage(e.message.toString())
        }

        override fun onCodeSent(
            verificationId: String,
            token: ForceResendingToken,
        ) {
            updateOTP(verificationId)
            updateResendToken(token)
        }
    }

    fun observeSignUpState(activity: Activity, activityResult: ActivityResult) {
        viewModelScope.launch {
            if (activityResult.resultCode == Activity.RESULT_OK) {
                try {
                    val googleCredential =
                        oneTapClient.getSignInCredentialFromIntent(activityResult.data)
                    mainViewModel.updateDialogState(DownloadDialogState.Show)
//                    downloadDialog.showDownloadDialog()
                    val firebaseCredential =
                        GoogleAuthProvider.getCredential(googleCredential.googleIdToken, null)
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(activity) { task ->
                            googleSignInExecution(task, firebaseCredential, googleCredential)
                        }
                } catch (ex: Exception) {
                    mainViewModel.popupMessage(ex.toString())
                }
            }
        }
    }

    private fun googleSignInExecution(
        task: Task<AuthResult>,
        firebaseCredential: AuthCredential,
        googleCredential: SignInCredential
    ) {
        viewModelScope.launch {
            if (task.isSuccessful) {
                firebaseAuth.currentUser!!.reauthenticate(firebaseCredential)
                    .addOnCompleteListener {
                        firebaseAuth.currentUser!!.updateEmail(googleCredential.id)
                        firebaseAuth.currentUser!!.updateProfile(userProfileChangeRequest {
                            displayName = googleCredential.displayName
                        })
                    }
                googleButtonPressNavigation(task, firebaseCredential, googleCredential)
            } else {
                mainViewModel.popupMessage(task.exception.toString())
            }
        }
    }

    private fun googleButtonPressNavigation(
        task: Task<AuthResult>,
        firebaseCredential: AuthCredential,
        googleCredential: SignInCredential
    ) {
        firebaseRealtimeDatabaseUserReference.child(firebaseAuth.currentUser!!.uid)
            .child("phoneNumber").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    viewModelScope.launch {
                        mainViewModel.updateDialogState(DownloadDialogState.Dismiss)
//                        downloadDialog.cancelDownloadDialog()
                        if (dataSnapshot.value != null) {
                            /** USER HAS PHONE NUMBER **/
                            _signUpState.value = SignUpState.SignedUp
                            mainViewModel.navigate(fragment = R.id.main_fragment)
                            mainViewModel.popupMessage(resourceManager.getString(R.string.successful_authentication_message))
                        } else {
                            /** USER DOESN'T HAS PHONE NUMBER **/
                            _signUpState.value = SignUpState.UnsignedUp
                            firebaseAuth.currentUser!!.linkWithCredential(
                                firebaseCredential
                            ).addOnCompleteListener {
                                if (task.isSuccessful) {
                                    viewModelScope.launch {
                                        firebaseAuth.currentUser!!.updateProfile(
                                            userProfileChangeRequest {
                                                displayName = googleCredential.displayName
                                                if (googleCredential.profilePictureUri != null) {
                                                    photoUri = googleCredential.profilePictureUri
                                                }
                                            })
                                        firebaseAuth.currentUser!!.updateEmail(
                                            googleCredential.id
                                        )
                                        mainViewModel.navigate(fragment = R.id.phone_number_fragment)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    fun phoneNumberPressNavigation(navigationFragment: Int) {
        viewModelScope.launch {
            mainViewModel.navigate(fragment = navigationFragment)
        }
    }

    fun signIn(
        activity: Activity,
        activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
//        downloadDialog.showDownloadDialog()
        mainViewModel.updateDialogState(DownloadDialogState.Show)
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(activity) { result ->
                val intentSenderRequest =
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                activityResultLauncher.launch(intentSenderRequest)
//                downloadDialog.cancelDownloadDialog()
                mainViewModel.updateDialogState(DownloadDialogState.Dismiss)
            }.addOnFailureListener(activity) { e ->
                viewModelScope.launch {
                    mainViewModel.popupMessage(e.message!!)
                }
            }
    }

    fun verifyPhoneNumber(
        activity: Activity,
        phoneNumber: String,
        isResend: Boolean,
        token: ForceResendingToken?
    ) {
        if (!isResend) {
            mainViewModel.updateDialogState(DownloadDialogState.Show)
//            downloadDialog.showDownloadDialog()
        }
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(if (isResend) callbackOTP else callbackPhoneNumber)
            .also { if (isResend) it.setForceResendingToken(token!!) }// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential
    ) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        when (_signInState.value) {
                            SignInState.Google -> {
                                googleSignIn(credential)
                            }

                            SignInState.PhoneNumber -> {
                                phoneNumberSignIn(credential)
                            }

                            SignInState.UnsignedIn -> {}
                        }
                        _isWrongOTP.value = false
                    } else {
                        _isWrongOTP.value = true
                    }
                }
            }
    }

    private fun googleSignIn(credential: PhoneAuthCredential) {
        firebaseAuth.currentUser!!.updatePhoneNumber(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    firebaseRealtimeDatabaseUserReference.child(
                        firebaseAuth.currentUser!!.uid
                    )
                        .setValue(
                            User(
                                photo = firebaseAuth.currentUser!!.photoUrl.toString(),
                                displayName = firebaseAuth.currentUser!!.displayName,
                                email = firebaseAuth.currentUser!!.email,
                                phoneNumber = firebaseAuth.currentUser!!.phoneNumber
                            )
                        )
                    mainViewModel.navigate(
                        fragment = R.id.register_fragment,
                        bundle = bundleOf("credentials" to credential)
                    )
                } else {
                    mainViewModel.popupMessage(it.exception!!.message.toString())
                }
            }
    }

    private fun phoneNumberSignIn(credential: PhoneAuthCredential) {
        if (firebaseAuth.currentUser!!.email != null) {
            updateSignUpState(SignUpState.SignedUp)
            mainViewModel.navigate(fragment = R.id.main_fragment)
        } else {
            firebaseAuth.currentUser!!.linkWithCredential(credential)
            updateSignUpState(SignUpState.UnsignedUp)
            mainViewModel.navigate(
                fragment = R.id.register_fragment,
                bundle = bundleOf(
                    "credentials" to credential
                )
            )
        }
    }

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

    fun updateResendToken(resendToken: ForceResendingToken) {
        viewModelScope.launch {
            _resendToken.emit(resendToken)
        }
    }

    fun updateSignInState(signInState: SignInState) {
        viewModelScope.launch {
            _signInState.update { signInState }
        }
    }

    fun updateSignUpState(signUpState: SignUpState) {
        viewModelScope.launch {
            _signUpState.update { signUpState }
        }
    }

    fun updateInputFieldErrorState(
        view: TextInputLayout,
        message: String,
        isErrorEnabled: Boolean
    ) {
        view.error = message
        view.isErrorEnabled = isErrorEnabled
    }

}