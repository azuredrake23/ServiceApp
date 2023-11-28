package com.example.serviceapp.ui.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.data.models.ValidationState
import com.example.serviceapp.databinding.AccountFragmentBinding
import com.example.serviceapp.domain.view_models.MainViewModel
import com.example.serviceapp.domain.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.ui.common_fragments.AccountFragment
import com.example.serviceapp.utils.DialogType
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
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
        layoutList: List<TextInputLayout>,
        validationList: List<ValidationState>
    ) {
        with(firebaseViewModel) {
            setOnClickListeners(layoutList)
            if (validationList.all { it is ValidationState.Success }) {
                val credential =
                    if (dialogType == DialogType.DELETE || dialogType == DialogType.PASSWORD) {
                        EmailAuthProvider
                            .getCredential(
                                binding.changeEmailView.text.toString(),
                                layoutList[0].editText!!.text.toString()
                            )
                    } else {
                        EmailAuthProvider
                            .getCredential(
                                binding.changeEmailView.text.toString(),
                                layoutList[1].editText!!.text.toString()
                            )
                    }
                firebaseAuth.currentUser!!.reauthenticate(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            dialogHandler(dialog, dialogType, layoutList)
                        } else {
                            if (dialogType == DialogType.DELETE || dialogType == DialogType.PASSWORD) {
                                updateInputFieldErrorState(
                                    layoutList[0],
                                    context.getString(R.string.incorrect_pass_message),
                                    true
                                )
                            } else {
                                updateInputFieldErrorState(
                                    layoutList[1],
                                    context.getString(R.string.incorrect_email_message),
                                    true
                                )
                            }
                        }
                    }
            } else {
                setErrors(context, layoutList, validationList)
            }
        }
    }

    private fun dialogHandler(
        dialog: AlertDialog,
        dialogType: DialogType,
        layoutList: List<TextInputLayout>
    ) {
        with(firebaseViewModel) {
            when (dialogType) {
                DialogType.USERNAME -> {
                    binding.changeUsernameView.text = layoutList[0].editText!!.text.toString()
                    firebaseViewModel.firebaseAuth.currentUser!!.updateProfile(
                        userProfileChangeRequest {
                            displayName = layoutList[0].editText!!.text.toString()
                        }).addOnCompleteListener {
                        showToast(
                            context,
                            context.getString(R.string.username_changed_message)
                        )
                        dialog.dismiss()
                    }

                }

                DialogType.EMAIL -> {
                    binding.changeEmailView.text = layoutList[0].editText!!.text.toString()
                    firebaseViewModel.firebaseAuth.currentUser!!.updateEmail(layoutList[0].editText!!.text.toString())
                        .addOnCompleteListener {
                            showToast(
                                context,
                                context.getString(R.string.email_changed_message)
                            )
                            dialog.dismiss()
                        }
                }

                DialogType.PASSWORD -> {
                    firebaseAuth.currentUser!!.updatePassword(layoutList[1].editText!!.text.toString())
                        .addOnCompleteListener {
                            showToast(
                                context,
                                context.getString(R.string.password_changed_message)
                            )
                            dialog.dismiss()
                        }
                }

                DialogType.DELETE -> {
                    firebaseAuth.currentUser!!.delete()
                        .addOnCompleteListener { delete ->
                            if (delete.isSuccessful) {
                                dialog.dismiss()
                                firebaseAuth.signOut()
                                mainViewModel.navigate(R.id.login_fragment)
                                showToast(
                                    context,
                                    context.getString(R.string.account_deleted_message)
                                )
                            }
                        }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setOnClickListeners(layoutList: List<TextInputLayout>) {
        layoutList.forEach {
            it.editText!!.setOnTouchListener { _, _ ->
                it.isErrorEnabled = false
                false
            }
        }
    }
}