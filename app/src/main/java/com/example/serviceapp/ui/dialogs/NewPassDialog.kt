package com.example.serviceapp.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.AccountFragmentBinding
import com.example.serviceapp.data.models.ValidationState
import com.example.serviceapp.utils.DialogType
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class NewPassDialog(
    val context: Context,
    val binding: AccountFragmentBinding,
    private val firebaseAuth: FirebaseAuth
) : Dialog {

    override fun setPositiveClickListener(
        dialog: AlertDialog,
        layoutList: List<TextInputLayout>,
        validationList: List<ValidationState>
    ) {
        setOnClickListeners(layoutList)
        if (validationList.all { it is ValidationState.Success }) {
            val credential = EmailAuthProvider
                .getCredential(
                    binding.changeEmailView.text.toString(),
                    layoutList[0].editText!!.text.toString()
                )
            firebaseAuth.currentUser!!.reauthenticate(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseAuth.currentUser!!.updatePassword(
                            layoutList[1].editText!!.text.toString()
                        )
                        showToast(
                            context,
                            context.getString(R.string.password_changed_message)
                        )
                        dialog.dismiss()
                    } else {
                        layoutList[0].error = context.getString(R.string.incorrect_pass_message)
                    }
                }
        } else {
            setErrors(context, validationList, layoutList)
        }
    }
}