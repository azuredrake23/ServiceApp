package com.example.serviceapp.ui.fragments.authorization

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.PhoneNumberFragmentBinding
import com.example.serviceapp.ui.fragments.MainFragmentDirections
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class PhoneNumberFragment : Fragment(R.layout.phone_number_fragment) {
    private val binding by viewBinding(PhoneNumberFragmentBinding::bind)

    var countryCode = "+91"
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private var phoneNumber = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setPhoneAuth(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    showToast(requireContext(), getString(R.string.successful_authentication_message))
                } else {
                    // Sign in failed, display a message and update the UI
                    showToast(requireContext(), "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun setupUI() {
        with(binding) {
            ccp.setOnCountryChangeListener {
                countryCode = ccp.selectedCountryCode.toString()
            }
            continueButton.setOnClickListener {
                if (editTextPhone.text.isNotEmpty()) {
                    if (editTextPhone.text.length == 9) {
                        phoneNumber = "+" + ccp.selectedCountryCode.toString() + editTextPhone.text.toString()
                        setPhoneAuth(phoneNumber)
                    } else {
                        showToast(
                            requireContext(),
                            getString(R.string.correct_phone_number_message)
                        )
                    }
                } else {
                    showToast(requireContext(), getString(R.string.enter_number_message))
                }
            }
        }
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
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                showToast(requireContext(), e.message.toString())
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                showToast(requireContext(), e.message.toString())
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                showToast(requireContext(), e.message.toString())
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

            findNavController().navigate(R.id.otp_fragment, bundleOf("verificationId" to verificationId, "token" to token, "phoneNumber" to phoneNumber))
        }
    }
}