package com.example.serviceapp.domain.view_models.firebase_view_models

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
import com.example.serviceapp.data.common.utils.ResourceManager
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.data.models.DownloadDialogState
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.models.SignUpState
import com.example.serviceapp.data.models.User
import com.example.serviceapp.data.models.ValidationState
import com.example.serviceapp.databinding.RegisterFragmentBinding
import com.example.serviceapp.domain.view_models.MainViewModel
import com.example.serviceapp.utils.Constants
import com.example.serviceapp.utils.DialogType
import com.example.serviceapp.utils.TimerState
import com.example.serviceapp.utils.isInRange
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
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
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

//    private val _OTP = MutableSharedFlow<String>(replay = 1)
//    val OTP: SharedFlow<String> get() = _OTP
//
//    private val _resendToken = MutableSharedFlow<ForceResendingToken>()
//    val resendToken: SharedFlow<ForceResendingToken> get() = _resendToken

    private val _signInState = MutableStateFlow<SignInState>(SignInState.UnsignedIn)
    val signInState: StateFlow<SignInState> get() = _signInState

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.UnsignedUp)
    val signUpState: StateFlow<SignUpState> get() = _signUpState

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
            val googleCredential =
                oneTapClient.getSignInCredentialFromIntent(activityResult.data)
            mainViewModel.updateDialogState(DownloadDialogState.Show)
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleCredential.googleIdToken, null)
            updateGoogleCredentials(firebaseCredential)
            firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener {
                if (it.isSuccessful) {
                    googleUserUidCheck()
                } else {
                    mainViewModel.warningDialog(it.exception!!.message.toString())
                }
            }
        }
    }

    private fun googleUserUidCheck() {
        viewModelScope.launch {
            with(mainViewModel) {
                firebaseRealtimeDatabaseUserReference.child(firebaseAuth.currentUser!!.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            updateDialogState(DownloadDialogState.Dismiss)
                            if (dataSnapshot.exists()) {
                                _signUpState.value = SignUpState.SignedUp
                                navigate(R.id.main_fragment)
                            } else {
                                _signUpState.value = SignUpState.UnsignedUp
                                navigate(R.id.phone_number_fragment)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            warningDialog(resourceManager.getString(R.string.database_error_message))
                        }
                    })
            }
        }
    }

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
    var leftTime = 0

    fun verifyPhoneNumber(
        activity: Activity,
        phoneNumber: String,
        isResend: Boolean,
        token: ForceResendingToken?
    ) {
        if (_timerValue.value is TimerState.Processing) {
            updateTimerValue(TimerState.Processing(leftTime))
            return
        }
        val timer = object: CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                leftTime = (millisUntilFinished/1000).toInt()
            }

            override fun onFinish() {
                updateTimerValue(TimerState.Stopped)
                leftTime = 0
            }
        }
        timer.start()
        updateTimerValue(TimerState.Processing())
        updatePhoneNumber(phoneNumber)
        if (!isResend) {
            mainViewModel.updateDialogState(DownloadDialogState.Show)
        }
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callback)
            .also { if (isResend) it.setForceResendingToken(token!!) }// OnVerificationStateChangedCallbacks
            .build()
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
                    firebaseAuth.currentUser!!.updatePhoneNumber(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            updateIsWrongOTP(false)
                            setNewValueToRealtimeDatabase()
                            navigate(R.id.main_fragment)
                        } else {
                            updateIsWrongOTP(true)
                            warningDialog(it.exception!!.message.toString())
                            //разобраться почему не работает warning dialog здесь
                        }
                    }
                }

                SignInState.PhoneNumber -> {
                    firebaseRealtimeDatabaseUserReference.child(firebaseAuth.currentUser!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    updateSignUpState(SignUpState.SignedUp)
                                    navigate(R.id.main_fragment)
                                } else {
                                    firebaseAuth.currentUser!!.updatePhoneNumber(credential)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                updateIsWrongOTP(false)
                                                updateSignUpState(SignUpState.UnsignedUp)
                                                navigate(R.id.register_fragment)
                                            } else {
                                                updateIsWrongOTP(true)
                                                warningDialog(it.exception!!.message.toString())
                                            }
                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                warningDialog(resourceManager.getString(R.string.database_error_message))
                            }
                        })


                }

                SignInState.UnsignedIn -> {}
            }

        }
    }

    /********** REGISTER FRAGMENT METHODS **********/

    fun initRegisterFragmentView(context: Context, binding: RegisterFragmentBinding) {
        with(binding) {
            if (signInState.value == SignInState.Google) {
                nameInputLayout.visibility = View.GONE
                emailInputLayout.visibility = View.GONE
            }
        }

        continueButtonListener(context, binding) {
            firebaseAuth.currentUser!!.updateProfile(userProfileChangeRequest {
                displayName = binding.nameEnter.text.toString()
            })
            firebaseAuth.currentUser!!.reauthenticate(
                phoneCredentials.value!!
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    updateSignUpState(SignUpState.SignedUp)
                    firebaseAuth.currentUser!!.updateEmail(binding.emailEnter.text.toString())
                    firebaseAuth.currentUser!!.updatePassword(binding.passwordEnter.text.toString())
                    setNewValueToRealtimeDatabase()
                    mainViewModel.navigate(R.id.main_fragment)
                } else {
                    mainViewModel.warningDialog(it.exception!!.message.toString())
                }
            }
        }
    }

    private fun continueButtonListener(
        context: Context,
        binding: RegisterFragmentBinding,
        realization: () -> Unit
    ) {
        with(binding) {
            continueButton.setOnClickListener {
                mainViewModel.updateDialogState(DownloadDialogState.Show)
                var validationList = listOf<ValidationState>()
                var validationInputList = listOf<TextInputLayout>()
                when (signInState.value) {
                    SignInState.Google -> {
                        validationList = listOf(
                            validatePassword(passwordEnter.text.toString())
                        )
                        validationInputList = listOf(
                            passwordInputLayout
                        )
                    }

                    SignInState.PhoneNumber -> {
                        validationList = listOf(
                            validateName(nameEnter.text.toString()),
                            validateEmail(emailEnter.text.toString()),
                            validatePassword(passwordEnter.text.toString())
                        )
                        validationInputList = listOf(
                            nameInputLayout,
                            emailInputLayout,
                            passwordInputLayout
                        )
                    }

                    SignInState.UnsignedIn -> {}
                }

                if (validationList.all { it is ValidationState.Success }) {
                    realization.invoke()
                } else {
                    setErrors(context, validationInputList, validationList)
                }
                mainViewModel.updateDialogState(DownloadDialogState.Dismiss)
            }
        }
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

    fun updateTimerValue(timerValue: TimerState){
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

    fun updateSignUpState(signUpState: SignUpState) {
        viewModelScope.launch {
            _signUpState.update { signUpState }
        }
    }

    fun validateFields(
        dialogType: DialogType,
        validationList: List<TextInputLayout>
    ): List<ValidationState> {
        val list = mutableListOf<ValidationState>()
        return if (validationList.isEmpty()) list
        else {
            val str1 = validationList[0].editText!!.text.toString()
            var str2 = ""
            if (validationList.size != 1) {
                str2 = validationList[1].editText!!.text.toString()
            }
            when (dialogType) {
                DialogType.USERNAME -> {
                    list.add(validateName(str1))
                    list.add(validatePassword(str2))
                }

                DialogType.EMAIL -> {
                    list.add(validateEmail(str1))
                    list.add(validatePassword(str2))
                }

                DialogType.PASSWORD -> {
                    list.add(validatePassword(str1))
                    list.add(validateNewPassword(str1, str2))
                }

                DialogType.DELETE -> {
                    list.add(validatePassword(str1))
                }
            }
            list
        }
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
        // сделать валидацию для email - regex и общую для всех остальных полей - длина от 6 до 15 символов
    }

    fun validatePassword(src: String): ValidationState {
        if (src.isEmpty()) {
            return ValidationState.Error(R.string.enter_value_message)
        }
        if (!src.length.isInRange(6, 15)) {
            return ValidationState.Error(R.string.wrong_password_length_message)
        }
        return ValidationState.Success(src)
    }

    fun validateNewPassword(password: String, newPassword: String): ValidationState {
        if (newPassword.isEmpty()) {
            return ValidationState.Error(R.string.enter_value_message)
        }
        if (!password.length.isInRange(6, 15)) {
            return ValidationState.Error(R.string.wrong_password_length_message)
        }
        if (password != newPassword) return ValidationState.Error(R.string.password_mismatch_message)
        return ValidationState.Success(newPassword)
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

    fun setNewValueToRealtimeDatabase() {
        firebaseRealtimeDatabaseUserReference.child(firebaseAuth.currentUser!!.uid)
            .setValue(
                User(
                    firebaseAuth.currentUser!!.photoUrl.toString(),
                    firebaseAuth.currentUser!!.displayName,
                    firebaseAuth.currentUser!!.email,
                    firebaseAuth.currentUser!!.phoneNumber
                )
            )
    }

}