package com.example.serviceapp.ui.firebase_fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.PreferenceManager
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.PhoneNumberFragmentBinding
import com.example.serviceapp.ui.view_models.database_view_models.UserDatabaseViewModel
import com.example.serviceapp.ui.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.utils.DownloadDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PhoneNumberFragment : Fragment(R.layout.phone_number_fragment) {
    private val binding by viewBinding(PhoneNumberFragmentBinding::bind)
    private val userViewModel: UserDatabaseViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

    private lateinit var preferenceManager: PreferenceManager

    var countryCode = ""
    private lateinit var phoneNumber: String

    private val firebaseAuth by lazy {
        firebaseViewModel.firebaseAuth
    }
    private val downloadDialog by lazy {
        DownloadDialog(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferenceManager = PreferenceManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun verifyPhoneNumber(phoneNumber: String) {
        downloadDialog.showDownloadDialog()
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun initFragment() {
        with(binding) {
            countryCode =
                preferenceManager.get(resources.getString(R.string.country_code), "+91")
            ccp.setOnCountryChangeListener {
                countryCode = ccp.selectedCountryCode.toString()
                preferenceManager.put(getString(R.string.country_code), ccp.selectedCountryCode)
            }
            continueButton.setOnClickListener {
                if (editTextPhone.text.isNotEmpty()) {
                    if (editTextPhone.text.length == 9) {
                        phoneNumber =
                            "+" + ccp.selectedCountryCode.toString() + editTextPhone.text.toString()
                        verifyPhoneNumber(phoneNumber)
                    } else {
                        showToast(
                            requireContext(),
                            getString(R.string.correct_phone_number_message)
                        )
                    }
                } else {
                    editTextPhone.error = getString(R.string.enter_value_message)
                    showToast(requireContext(), getString(R.string.enter_number_message))
                }
            }
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e)

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
            downloadDialog.cancelDownloadDialog()
            findNavController().navigate(
                R.id.otp_fragment,
                bundleOf(
                    "verificationId" to verificationId,
                    "token" to token,
                    "phoneNumber" to phoneNumber
                )
            )
        }
    }
}