package com.example.serviceapp.ui.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.serviceapp.R
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.models.ValidationState
import com.example.serviceapp.databinding.AccountFragmentBinding
import com.example.serviceapp.ui.view_models.FirebaseViewModel
import com.example.serviceapp.ui.view_models.MainViewModel
import com.example.serviceapp.utils.DialogType
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest


class Dialog(
    val context: Context,
    val binding: AccountFragmentBinding,
    private val mainViewModel: MainViewModel,
    private val firebaseViewModel: FirebaseViewModel
) {
    fun setPositiveClickListener(
        dialog: AlertDialog,
        dialogType: DialogType,
        field: TextInputLayout,
        validationList: List<ValidationState>
    ) {
        with(firebaseViewModel) {
            setOnClickListeners(field)
            if (validationList.all { it is ValidationState.Success }) {
                when (signInState.value) {
                    SignInState.Google -> googleCredentials.value
                    SignInState.PhoneNumber -> phoneCredentials.value
                    SignInState.GoogleAndPhoneNumber -> googleCredentials.value //could use phoneCredentials too
                    SignInState.UnsignedIn -> null
                }?.let { it ->
                    firebaseAuth.currentUser!!.reauthenticate(
                        it
                    )
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                dialogHandler(dialog, dialogType, field)
                            } else {
                                when (dialogType) {
                                    DialogType.USERNAME, DialogType.EMAIL -> {
                                        updateInputFieldErrorState(
                                            field,
                                            context.getString(R.string.incorrect_pass_message),
                                            true
                                        )
                                    }
                                }
                            }
                        }
                }
            } else {
                setErrors(context, listOf(field), validationList)
            }
        }
    }

    private fun dialogHandler(
        dialog: AlertDialog,
        dialogType: DialogType,
        field: TextInputLayout
    ) {
        with(firebaseViewModel) {
            when (dialogType) {
                DialogType.USERNAME -> {
                    binding.changeUsernameView.text = field.editText!!.text.toString()
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(field.editText!!.text.toString())
                        .build()
                    firebaseAuth.currentUser!!.updateProfile(profileUpdates)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                mainViewModel.popupMessage(context.getString(R.string.username_changed_message))
                            } else {
                                mainViewModel.popupMessage(it.exception!!.message.toString())
                            }
                            dialog.dismiss()
                        }
                }

                DialogType.EMAIL -> {
                    binding.changeEmailView.text = field.editText!!.text.toString()
                    firebaseViewModel.firebaseAuth.currentUser!!.verifyBeforeUpdateEmail(field.editText!!.text.toString())
                        .addOnCompleteListener {
                            mainViewModel.popupMessage(context.getString(R.string.email_changed_message))
                            dialog.dismiss()
                        }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setOnClickListeners(field: TextInputLayout) {
        field.editText!!.setOnTouchListener { _, _ ->
            field.isErrorEnabled = false
            false
        }
    }
}