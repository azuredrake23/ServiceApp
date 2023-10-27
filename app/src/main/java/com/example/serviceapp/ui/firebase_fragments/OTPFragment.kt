package com.example.serviceapp.ui.firebase_fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContentProviderCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.data.models.DownloadDialogState
import com.example.serviceapp.databinding.OtpFragmentBinding
import com.example.serviceapp.domain.view_models.database_view_models.UserDatabaseViewModel
import com.example.serviceapp.domain.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.models.SignUpState
import com.example.serviceapp.domain.view_models.MainViewModel
import com.example.serviceapp.utils.DownloadDialog
import com.google.android.play.integrity.internal.m
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
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OTPFragment : Fragment(R.layout.otp_fragment) {
    private val binding by viewBinding(OtpFragmentBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

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
        buttonListeners()
    }

    private fun buttonListeners() {
        with(binding) {
            verifyButton.setOnClickListener {
                if (typedOTP.length == 6 && !typedOTP.contains(" ")) {
                    onUserTryToVerify()
                }
            }
            resendTv.setOnClickListener {
                firebaseViewModel.verifyPhoneNumber(
                    requireActivity(),
                    fragmentPhoneNumber,
                    true,
                    fragmentResendToken!!
                )
                resendOTPTvVisibility()
            }
        }
    }

    private fun setObservers() {
        with (mainViewModel){
            popupValue.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                showToast(requireContext(), it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            navigateFragmentValue.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                findNavController().navigate(
                    it.first, it.second
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            downloadDialogState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                checkDownloadDialogState(requireActivity(), it)
            }
        }
        with(firebaseViewModel) {
            phoneNumber.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
                    fragmentPhoneNumber = it
                }.launchIn(lifecycleScope)
            OTP.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                    fragmentOTP = it
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            resendToken.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
                    fragmentResendToken = it
                }.launchIn(lifecycleScope)

            isWrongOTP.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                if (it) {
                    setEmptyEditTexts()
                    typedOTP = "      "
                    mainViewModel.popupMessage(getString(R.string.wrong_otp_message))
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            progressBarVisibility.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                when (it) {
                    View.INVISIBLE -> binding.progressBar.visibility = View.INVISIBLE
                    View.VISIBLE -> binding.progressBar.visibility = View.VISIBLE
                    View.GONE -> binding.progressBar.visibility = View.GONE
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun initFragment() {
        updateFields()
        resendOTPTvVisibility()
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun resendOTPTvVisibility() {
        with(binding) {
            setEmptyEditTexts()
            resendTv.visibility = View.INVISIBLE
            resendTv.isEnabled = false

            Handler(Looper.getMainLooper()).postDelayed({
                resendTv.visibility = View.VISIBLE
                resendTv.isEnabled = true
            }, 60000)
        }
    }

    private fun updateFields() {
        with(firebaseViewModel) {
            updatePhoneNumber(requireArguments().getString("phoneNumber")!!)
            updateOTP(requireArguments().getString("verificationId")!!)
            updateResendToken(requireArguments().getParcelable("token")!!)
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

    private fun onUserTryToVerify() {
        if (typedOTP.length == 6 && !typedOTP.contains(" ")) {
            with(binding) {
                val credential: PhoneAuthCredential =
                    PhoneAuthProvider.getCredential(fragmentOTP, typedOTP)
                progressBar.visibility = View.VISIBLE
                firebaseViewModel.signInWithPhoneAuthCredential(credential)
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