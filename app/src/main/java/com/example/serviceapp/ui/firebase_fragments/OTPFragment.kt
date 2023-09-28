package com.example.serviceapp.ui.firebase_fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.OtpFragmentBinding
import com.example.serviceapp.ui.view_models.database_view_models.UserDatabaseViewModel
import com.example.serviceapp.ui.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.utils.SignInState
import com.example.serviceapp.utils.SignUpState
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OTPFragment : Fragment(R.layout.otp_fragment) {
    private val binding by viewBinding(OtpFragmentBinding::bind)
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()
    private val userViewModel: UserDatabaseViewModel by activityViewModels()

    private val firebaseAuth by lazy {
        firebaseViewModel.firebaseAuth
    }
    private val firebaseRealtimeDatabaseUserReference by lazy {
        firebaseViewModel.firebaseRealtimeDatabaseUserRef
    }

    private lateinit var currentUser: User
    private var fragmentPhoneNumber: String = ""
    private var fragmentOTP: String = ""
    private var fragmentResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var typedOTP: String = "      "

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
        initFragment()
    }

    private fun setListeners() {
        addTextChangeListener()
    }

    private fun setObservers() {
        with(firebaseViewModel) {
            user.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .onEach {
                    currentUser = it
                }
            phoneNumber.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .onEach {
                    fragmentPhoneNumber = it
                }.launchIn(lifecycleScope)
            OTP.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach {
                    fragmentOTP = it
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            resendToken.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .onEach {
                    fragmentResendToken = it
                }.launchIn(lifecycleScope)
        }
        with(userViewModel) {
//            user.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//                .onEach {
//                    currentUser = it
//                }
        }
    }

    private fun initFragment() {
        updateFields()
        resendOTPTvVisibility()
        with(binding) {
            verifyButton.setOnClickListener {
                if (typedOTP.length == 6 && !typedOTP.contains(" ")) {
                    onUserTryToVerify()
                }
            }
            resendTv.setOnClickListener {
                resendVerificationCode()
                resendOTPTvVisibility()
            }
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun resendOTPTvVisibility() {
        with(binding) {
            setEmptyEditTexts()
            resendTv.visibility = View.INVISIBLE
            resendTv.isEnabled = false

            Handler().postDelayed({
                resendTv.visibility = View.VISIBLE
                resendTv.isEnabled = true
            }, 60000)
        }
    }

    private fun updateFields() {
        firebaseViewModel.updatePhoneNumber(requireArguments().getString("phoneNumber")!!)
        firebaseViewModel.updateOTP(requireArguments().getString("verificationId")!!)
        firebaseViewModel.updateResendToken(requireArguments().getParcelable("token")!!)

    }

    private fun setEmptyEditTexts() {
        with(binding) {
            otpEditText1.setText("")
            otpEditText2.setText("")
            otpEditText3.setText("")
            otpEditText4.setText("")
            otpEditText5.setText("")
            otpEditText6.setText("")
        }
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(fragmentPhoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(fragmentResendToken!!)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    showToast(requireContext(), e.message.toString())
                }

                is FirebaseTooManyRequestsException -> {
                    showToast(requireContext(), e.message.toString())
                }

                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    showToast(requireContext(), e.message.toString())
                }
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            firebaseViewModel.updateOTP(verificationId)
            firebaseViewModel.updateResendToken(token)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        when (firebaseViewModel.getSignInState()) {
            SignInState.Google -> {
                firebaseAuth.currentUser!!.updatePhoneNumber(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseRealtimeDatabaseUserReference.child(firebaseAuth.currentUser!!.uid)
                            .setValue(
                                User(
                                    photo = firebaseAuth.currentUser!!.photoUrl.toString(),
                                    displayName = firebaseAuth.currentUser!!.displayName,
                                    email = firebaseAuth.currentUser!!.email,
                                    phoneNumber = firebaseAuth.currentUser!!.phoneNumber
                                )
                            )
                        findNavController().navigate(R.id.register_fragment, bundleOf("credentials" to credential))
                    } else {
                        showToast(requireContext(), it.exception!!.message.toString())
                    }
                }
            }

            SignInState.PhoneNumber -> {
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            if (firebaseAuth.currentUser!!.email != null) {
                                firebaseViewModel.updateSignUpState(SignUpState.SignedUp)
                                findNavController().navigate(R.id.main_fragment)
                            } else {
                                firebaseAuth.currentUser!!.linkWithCredential(credential)
                                firebaseViewModel.updateSignUpState(SignUpState.UnsignedUp)
                                findNavController().navigate(R.id.register_fragment, bundleOf("credentials" to credential))
                            }
                        } else {
                            setEmptyEditTexts()
                            typedOTP = "      "
                            showToast(
                                requireContext(),
                                getString(R.string.wrong_otp_message)
                            )
                        }
                        binding.progressBar.visibility = View.INVISIBLE
                    }
            }

            SignInState.UnsignedIn -> {
            }

        }
    }

    private fun onUserTryToVerify() {
        if (typedOTP.length == 6 && !typedOTP.contains(" ")) {
            with(binding) {
                val credential: PhoneAuthCredential =
                    PhoneAuthProvider.getCredential(fragmentOTP, typedOTP)
                progressBar.visibility = View.VISIBLE
                signInWithPhoneAuthCredential(credential)
            }
        }
    }

    private fun changedTypedOTP(symbols: String, index: Int): String {
        val chars = typedOTP.toCharArray()
        if (symbols.length == 1) {
            chars[index - 1] = symbols[0]
        } else if (symbols.isEmpty()) {
            chars[index - 1] = ' '
        }
//        if (symbols.length == 2) {
//            chars[index - 1] = symbols[0]
//            chars[index] = symbols[1]
//        }
        typedOTP = String(chars)
        return typedOTP
    }

    private fun checkOTPDigit(
        p0: CharSequence?,
        editText1: EditText,
        editText2: EditText? = null
    ) {
        var isNextSymbol = false
        if (p0.toString().length == 2) {
            if (editText1.selectionStart == 2) {
                isNextSymbol = true
            }
            editText1.setText(p0!!.substring(0, 1))
            if (editText2 != null && isNextSymbol) {
                editText2.setText(p0.substring(1, 2))
                editText2.setSelection(1)
                editText2.requestFocus()
            }
        }
    }

    private fun checkVerificationField(
        p0: CharSequence?,
        indexOfElement: Int
    ) {
        if (p0.toString().length == 1 || p0.toString().isEmpty()) {
            typedOTP = changedTypedOTP(p0.toString(), indexOfElement)
            onUserTryToVerify()
        }
    }

    private fun EditText.addOnClickClearButtonListener(editTextPrev: EditText) {
        this.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && this.text.isEmpty()) {
                if (editTextPrev.text.isNotEmpty()) {
                    editTextPrev.setText("")
                }
                editTextPrev.requestFocus()
            }

            false
        }
    }

    private fun addTextChangeListener() {
        with(binding) {
            otpEditText1.addTextChangedListener(EditTextWatcher(otpEditText1))

            otpEditText2.addTextChangedListener(EditTextWatcher(otpEditText2))
            otpEditText2.addOnClickClearButtonListener(otpEditText1)

            otpEditText3.addTextChangedListener(EditTextWatcher(otpEditText3))
            otpEditText3.addOnClickClearButtonListener(otpEditText2)

            otpEditText4.addTextChangedListener(EditTextWatcher(otpEditText4))
            otpEditText4.addOnClickClearButtonListener(otpEditText3)

            otpEditText5.addTextChangedListener(EditTextWatcher(otpEditText5))
            otpEditText5.addOnClickClearButtonListener(otpEditText4)

            otpEditText6.addTextChangedListener(EditTextWatcher(otpEditText6))
            otpEditText6.addOnClickClearButtonListener(otpEditText5)
        }
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            with(binding) {
                when (view.id) {
                    R.id.otpEditText1 -> {
                        checkOTPDigit(p0, otpEditText1, otpEditText2)
                        checkVerificationField(p0, 1)
                    }

                    R.id.otpEditText2 -> {
                        checkOTPDigit(p0, otpEditText2, otpEditText3)
                        checkVerificationField(p0, 2)
                    }

                    R.id.otpEditText3 -> {
                        checkOTPDigit(p0, otpEditText3, otpEditText4)
                        checkVerificationField(p0, 3)
                    }

                    R.id.otpEditText4 -> {
                        checkOTPDigit(p0, otpEditText4, otpEditText5)
                        checkVerificationField(p0, 4)
                    }

                    R.id.otpEditText5 -> {
                        checkOTPDigit(p0, otpEditText5, otpEditText6)
                        checkVerificationField(p0, 5)
                    }

                    R.id.otpEditText6 -> {
                        otpEditText6.hideKeyboard()
                        checkOTPDigit(p0, otpEditText6)
                        checkVerificationField(p0, 6)
                    }
                }
            }
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}