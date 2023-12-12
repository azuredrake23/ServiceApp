package com.example.serviceapp.ui.view_models

import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.R
import com.example.serviceapp.data.utils.ResourceManager
import com.example.serviceapp.data.models.DownloadDialogState
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.models.SignUpState
import com.example.serviceapp.data.models.ValidationState
import com.example.serviceapp.databinding.RegisterFragmentBinding
import com.example.serviceapp.utils.Constants
import com.example.serviceapp.utils.DialogType
import com.example.serviceapp.utils.TimerState
import com.example.serviceapp.utils.isInRange
import com.example.serviceapp.utils.setUserToRealtimeDatabase
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val resourceManager: ResourceManager,
    val firebaseRealtimeDatabaseUserReference: DatabaseReference,
    private val signInRequest: BeginSignInRequest
) : ViewModel() {

    private val _googleCredentials = MutableLiveData<AuthCredential>()
    val googleCredentials: LiveData<AuthCredential> get() = _googleCredentials

    private val _phoneCredentials = MutableLiveData<PhoneAuthCredential>()
    val phoneCredentials: LiveData<PhoneAuthCredential> get() = _phoneCredentials

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private val _timerValue = MutableLiveData<TimerState>(TimerState.Stopped)
    val timerValue: LiveData<TimerState> get() = _timerValue

    private val _OTP = MutableLiveData<String>()
    val OTP: LiveData<String> get() = _OTP

    private val _resendToken = MutableLiveData<ForceResendingToken>()
    val resendToken: LiveData<ForceResendingToken> get() = _resendToken

    private val _isWrongOTP = MutableLiveData(false)
    val isWrongOTP: LiveData<Boolean> get() = _isWrongOTP

    private val _signInState = MutableStateFlow<SignInState>(SignInState.UnsignedIn)
    val signInState: StateFlow<SignInState> get() = _signInState

    private  val timer = object : CountDownTimer(Constants.timerValue, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            updateTimerValue(TimerState.Processing((millisUntilFinished / 1000).toInt()))
        }

        override fun onFinish() {
            updateTimerValue(TimerState.Stopped)
            this.cancel()
        }
    }

    private val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        }

        override fun onVerificationFailed(e: FirebaseException) {
            mainViewModel.warningDialog(e.message.toString())
        }

        override fun onCodeSent(
            verificationId: String,
            token: ForceResendingToken,
        ) {
            timer.start()
            updateTimerValue(TimerState.Processing())
            updateOTP(verificationId)
            updateResendToken(token)
            with(mainViewModel) {
                updateDialogState(DownloadDialogState.Dismiss)
                navigate(R.id.otp_fragment)
            }
        }
    }

    /********** LOGIN FRAGMENT METHODS **********/

    fun googleSignUpActivityResult(activityResult: ActivityResult) {
        viewModelScope.launch {
            mainViewModel.updateDialogState(DownloadDialogState.Show)
            val googleCredential =
                oneTapClient.getSignInCredentialFromIntent(activityResult.data)
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleCredential.googleIdToken, null)
            updateGoogleCredentials(firebaseCredential)
            firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener {
                if (it.isSuccessful) {
                    updateSignInState(SignInState.Google)
                    mainViewModel.updateDialogState(DownloadDialogState.Dismiss)
                    mainViewModel.navigate(R.id.main_fragment)
                } else {
                    mainViewModel.warningDialog(it.exception!!.message.toString())
                }
            }
        }
    }

//    private fun googleUserUidCheck() {
//        viewModelScope.launch {
//            with(mainViewModel) {
//                firebaseRealtimeDatabaseUserReference.child(firebaseAuth.currentUser!!.uid)
//                    .addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            updateDialogState(DownloadDialogState.Dismiss)
//                            if (dataSnapshot.exists()) {
//
//                            } else {
//                                _signUpState.value = SignUpState.UnsignedUp
//                                navigate(R.id.phone_number_fragment)
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            warningDialog(resourceManager.getString(R.string.database_error_message))
//                        }
//                    })
//            }
//        }
//    }

    fun signIn(
        activity: Activity,
        activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        mainViewModel.updateDialogState(DownloadDialogState.Show)
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(activity) { result ->
                val intentSenderRequest =
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                activityResultLauncher.launch(intentSenderRequest)
                mainViewModel.updateDialogState(DownloadDialogState.Dismiss)
            }.addOnFailureListener(activity) { e ->
                mainViewModel.warningDialog(e.message!!)
            }
    }

    fun isUserExists(
        activity: FragmentActivity,
        phoneNumber: String,
        isResend: Boolean,
        token: ForceResendingToken?
    ) {
//        viewModelScope.launch {
//            firebaseRealtimeDatabaseUserReference.orderByChild("phoneNumber")
//                .equalTo(phoneNumber)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                                for (data in dataSnapshot.children) {
//                                    val phone = data.child("phoneNumber").value.toString()
//                                    if (phone == phoneNumber) {
//                                        mainViewModel.popupMessage(resourceManager.getString(R.string.phone_registered_message))
//                                    } else {
//                                        verifyPhoneNumber(activity, phoneNumber, isResend, token)
//                                    }
//                                }
//                            mainViewModel.popupMessage(resourceManager.getString(R.string.phone_registered_message))
//                        } else {
//                            verifyPhoneNumber(activity, phoneNumber, isResend, token)
//                        }
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {}
//                })
//        }

    }

    /********** PHONE NUMBER FRAGMENT METHODS **********/

    fun verifyPhoneNumber(
        activity: Activity,
        phoneNumber: String,
        isResend: Boolean,
        token: ForceResendingToken?
    ) {
//        if (_timerValue.value is TimerState.Processing) {
//            updateTimerValue(TimerState.Processing(leftTime))
//            return
//        }
        updatePhoneNumber(phoneNumber)
        if (!isResend) {
            mainViewModel.updateDialogState(DownloadDialogState.Show)
        }
        val options = PhoneAuthOptions.newBuilder(firebaseAuth).apply {
                setPhoneNumber(phoneNumber) // Phone number to verify
                setTimeout(Constants.timerValue, TimeUnit.MILLISECONDS) // Timeout and unit
                setActivity(activity) // Activity (for callback binding)
                setCallbacks(callback)
                also { if (isResend) it.setForceResendingToken(token!!) }
            }.build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /********** OTP FRAGMENT METHODS **********/

    fun signInWithCredential(
        credential: PhoneAuthCredential
    ) {
        updatePhoneCredentials(credential)
        with(mainViewModel) {
            updateDialogState(DownloadDialogState.Dismiss)
            when (signInState.value) {
                SignInState.Google -> {
                    firebaseAuth.currentUser!!.linkWithCredential(credential).addOnCompleteListener { it ->
                        if (it.isSuccessful) {
                            firebaseAuth.currentUser!!.updatePhoneNumber(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        stopTimer()
                                        updateIsWrongOTP(false)
                                        navigate(R.id.main_fragment)
                                    } else {
                                        updateIsWrongOTP(true)
                                        warningDialog(it.exception!!.message.toString())
                                    }
                                }
                        } else {
                            updateIsWrongOTP(true)
                            warningDialog(it.exception!!.message.toString())
                        }
                    }
                }

                SignInState.PhoneNumber -> {
                    firebaseAuth.signInWithCredential(credential).addOnCompleteListener { it ->
                        if (it.isSuccessful) {
                            firebaseAuth.currentUser!!.updatePhoneNumber(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        stopTimer()
                                        updateIsWrongOTP(false)
                                        navigate(R.id.main_fragment)
                                    } else {
                                        updateIsWrongOTP(true)
                                        warningDialog(it.exception!!.message.toString())
                                    }
                                }
                        } else {
                            updateIsWrongOTP(true)
                            warningDialog(it.exception!!.message.toString())
                        }
                    }
                }

                SignInState.GoogleAndPhoneNumber -> {}

                SignInState.UnsignedIn -> {}
            }

        }
    }

    fun stopTimer(){
        updateTimerValue(TimerState.Stopped)
        timer.cancel()
    }

    /********** COMMON METHODS **********/

    fun updateGoogleCredentials(credentials: AuthCredential) {
        viewModelScope.launch {
            _googleCredentials.value = credentials
        }
    }

    fun updatePhoneCredentials(credentials: PhoneAuthCredential) {
        viewModelScope.launch {
            _phoneCredentials.value = credentials
        }
    }

    fun updatePhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            _phoneNumber.value = phoneNumber
        }
    }

    fun updateTimerValue(timerValue: TimerState) {
        viewModelScope.launch {
            _timerValue.value = timerValue
        }
    }

    fun updateOTP(otp: String) {
        viewModelScope.launch {
            _OTP.value = otp
        }
    }

    fun updateIsWrongOTP(isWrongOTP: Boolean) {
        viewModelScope.launch {
            _isWrongOTP.value = isWrongOTP
        }
    }

    fun updateResendToken(resendToken: ForceResendingToken) {
        viewModelScope.launch {
            _resendToken.value = resendToken
        }
    }

    fun updateSignInState(signInState: SignInState) {
        viewModelScope.launch {
            _signInState.update { signInState }
        }
    }

    fun validateFields(
        dialogType: DialogType,
        validationField: TextInputLayout
    ): List<ValidationState> {
        val list = mutableListOf<ValidationState>()
        if (dialogType == DialogType.USERNAME){
            list.add(validateName(validationField.editText!!.text.toString()))
        }else if (dialogType == DialogType.EMAIL){
            list.add(validateEmail(validationField.editText!!.text.toString()))
        }
        return list
    }

    fun validateName(src: String): ValidationState {
        if (src.isEmpty()) {
            return ValidationState.Error(R.string.enter_value_message)
        }
        if (!src.length.isInRange(0, 30)) {
            return ValidationState.Error(R.string.long_username_message)
        }
        return ValidationState.Success(src)
    }

    fun validateEmail(src: String): ValidationState {
        if (src.isEmpty()) {
            return ValidationState.Error(R.string.enter_value_message)
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
        if (!src.matches(emailRegex)) {
            return ValidationState.Error(R.string.incorrect_email_message)
        }
        return ValidationState.Success(src)
    }

    fun updateInputFieldErrorState(
        view: TextInputLayout,
        message: String,
        isErrorEnabled: Boolean
    ) {
        view.error = message
        view.isErrorEnabled = isErrorEnabled
    }

    fun setErrors(
        context: Context,
        validationInputList: List<TextInputLayout>,
        validationList: List<ValidationState>
    ) {
        validationList.forEachIndexed { index, validationState ->
            when (validationState) {
                is ValidationState.Success -> {
                    updateInputFieldErrorState(
                        validationInputList[index], "", false
                    )
                }

                is ValidationState.Error -> {
                    updateInputFieldErrorState(
                        validationInputList[index],
                        context.getString(validationState.messageStringId),
                        true
                    )
                }

                is ValidationState.Inactive -> {}
            }
        }
    }

}