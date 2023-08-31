package com.example.serviceapp.ui.fragments.authorization

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
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.OtpFragmentBinding
import com.google.android.play.integrity.internal.s
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext


class OTPFragment : Fragment(R.layout.otp_fragment) {
    private val binding by viewBinding(OtpFragmentBinding::bind)

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var phoneNumber: String
    private lateinit var OTP: String
    private var typedOTP: String = "      "
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        phoneNumber = requireArguments().getString("phoneNumber")!!
        OTP = requireArguments().getString("verificationId")!!
        resendToken = requireArguments().getParcelable("token")!!
        addTextChangeListener()
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
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
//                Log.d(TAG, "onVerificationCompleted:$credential")
//            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token
            OTP = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showToast(
                        requireContext(),
                        getString(R.string.successful_authentication_message)
                    )
                    findNavController().navigate(
                        R.id.main_fragment,
                        bundleOf("firebaseAuthUser" to firebaseAuth.currentUser)
                    )
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

    private fun onUserTryToVerify() {
        with(binding) {
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(OTP, typedOTP)
            progressBar.visibility = View.VISIBLE
            signInWithPhoneAuthCredential(credential)
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
            if (editText1.selectionStart == 2){
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
            if (typedOTP.length == 6 && !typedOTP.contains(" ")) {
                onUserTryToVerify()
            }
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