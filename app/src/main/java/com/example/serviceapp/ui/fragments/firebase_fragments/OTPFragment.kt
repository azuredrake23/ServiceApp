package com.example.serviceapp.ui.fragments.firebase_fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.system.Os.bind
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.models.DownloadDialogState
import com.example.serviceapp.data.utils.DataExtensions
import com.example.serviceapp.data.utils.DataExtensions.createWarningAlertDialog
import com.example.serviceapp.databinding.OtpFragmentBinding
import com.example.serviceapp.ui.view_models.FirebaseViewModel
import com.example.serviceapp.ui.view_models.MainViewModel
import com.example.serviceapp.utils.Constants
import com.example.serviceapp.utils.hideKeyboard
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
    }

    private fun setListeners() {
        addTextChangeListeners()
        buttonListeners()
    }

    private fun buttonListeners() {
        with(binding) {
            verifyButton.setOnClickListener {
                onUserTryToVerify()
            }
            resendTv.setOnClickListener {
                    firebaseViewModel.verifyPhoneNumber(
                        requireActivity(),
                        fragmentPhoneNumber,
                        isResend = true,
                        fragmentResendToken!!
                    )
//                if (firebaseViewModel.timerValue.value is TimerState.Stopped) {
                    resendOTPTvVisibility()
//                }
            }
        }
    }

    private fun setObservers() {
        with(mainViewModel) {
            navigateFragmentValue.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).onEach {
                findNavController().navigate(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            downloadDialogState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).onEach {
                checkDownloadDialogState(requireActivity(), it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            warningDialogMessage.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                createWarningAlertDialog(requireActivity(), it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
        with(firebaseViewModel) {
            phoneNumber.observe(viewLifecycleOwner) {
                fragmentPhoneNumber = it
            }
            OTP.observe(viewLifecycleOwner) {
                fragmentOTP = it
            }
            resendToken.observe(viewLifecycleOwner) {
                fragmentResendToken = it
            }
            isWrongOTP.observe(viewLifecycleOwner) {
                if (it) {
                    setEmptyEditTexts()
                    typedOTP = "      "
                    with(binding) {
                        progressBar.visibility = View.GONE
                        otpEditText1.requestFocus()
                    }
                }
            }
        }
    }

    private fun resendOTPTvVisibility() {
        with(binding) {
            setEmptyEditTexts()
            resendTv.visibility = View.GONE
            resendTv.isEnabled = false

            Handler(Looper.getMainLooper()).postDelayed({
                resendTv.visibility = View.VISIBLE
                resendTv.isEnabled = true
            }, Constants.timerValue)
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
                progressBar.visibility = View.VISIBLE
                mainViewModel.updateDialogState(DownloadDialogState.Show)
                val credential = PhoneAuthProvider.getCredential(fragmentOTP, typedOTP)
                firebaseViewModel.signInWithCredential(credential)
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

    private fun addTextChangeListeners() {
        with(binding) {
            otpEditText1.requestFocus()
            val listOfEditTexts = listOf(
                otpEditText1,
                otpEditText2,
                otpEditText3,
                otpEditText4,
                otpEditText5,
                otpEditText6
            )
            listOfEditTexts.onEachIndexed { index, editText ->
                editText.addTextChangedListener(EditTextWatcher(editText))
                if (editText != otpEditText1)
                    editText.addOnClickClearButtonListener(listOfEditTexts[index - 1])
                editText.setOnFocusChangeListener { view, b ->
                    listOfEditTexts.onEach {
                        it.backgroundTintList =
                            ColorStateList.valueOf(requireContext().getColor(R.color.space_y))
                    }
                    view.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.space_x))
                }
            }
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


}