package com.example.serviceapp.ui.firebase_fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.databinding.RegisterFragmentBinding
import com.example.serviceapp.domain.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.models.SignUpState
import com.example.serviceapp.domain.view_models.MainViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.userProfileChangeRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.register_fragment) {
    val binding by viewBinding(RegisterFragmentBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with(firebaseViewModel) {
            when (signInState.value) {
                SignInState.PhoneNumber -> {
                    with(binding) {
                        continueButton.setOnClickListener {
                            if (isValidationSuccessful()) {
                                firebaseAuth.currentUser!!.updateProfile(userProfileChangeRequest {
                                    displayName = nameEnter.text.toString()
                                })
                                firebaseAuth.currentUser!!.reauthenticate(
                                    requireArguments().getParcelable(
                                        "credentials"
                                    )!!
                                ).addOnCompleteListener {
                                    firebaseAuth.currentUser!!.updateEmail(emailEnter.text.toString())
                                    firebaseAuth.currentUser!!.updatePassword(passwordEnter.text.toString())
                                    mainViewModel.navigate(fragment = R.id.main_fragment)
                                }
                            } else {
                                isFieldValid(nameEnter, nameInputLayout)
                                isFieldValid(emailEnter, emailInputLayout)
                                isFieldValid(passwordEnter, passwordInputLayout)
                            }
                        }
                    }
                }

                SignInState.Google -> {
                    with(binding) {
                        nameEnter.isEnabled = false
                        nameEnter.setText(firebaseAuth.currentUser!!.displayName)
                        emailEnter.isEnabled = false
                        emailEnter.setText(firebaseAuth.currentUser!!.email)
                        firebaseViewModel.updateSignUpState(SignUpState.SignedUp)
                        continueButton.setOnClickListener {
                            if (isValidationSuccessful()) {
                                firebaseAuth.currentUser!!.updatePassword(passwordEnter.text.toString())
                                mainViewModel.navigate(fragment = R.id.main_fragment)
                            } else {
                                isFieldValid(nameEnter, nameInputLayout)
                                isFieldValid(emailEnter, emailInputLayout)
                                isFieldValid(passwordEnter, passwordInputLayout)
                            }
                        }
                    }
                }

                SignInState.UnsignedIn -> {}
            }
        }
    }


    fun isFieldValid(text: AppCompatEditText, layout: TextInputLayout): Boolean {
        if (text.toString() == "") {
            firebaseViewModel.updateInputFieldErrorState(
                layout,
                requireContext().getString(R.string.empty_field_error_message),
                true
            )
            return false
        }
        return true
    }

    private fun isValidationSuccessful(): Boolean {
        with(binding) {
            if (isFieldValid(nameEnter, nameInputLayout) && isFieldValid(emailEnter, emailInputLayout) && isFieldValid(passwordEnter, passwordInputLayout)) {
                return true
            }
            return false
        }
    }
}