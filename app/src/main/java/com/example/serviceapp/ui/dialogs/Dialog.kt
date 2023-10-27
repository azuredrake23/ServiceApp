package com.example.serviceapp.ui.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import com.example.serviceapp.data.models.ValidationState
import com.google.android.material.textfield.TextInputLayout

interface Dialog {
    fun setPositiveClickListener(
        dialog: AlertDialog,
        layoutList: List<TextInputLayout>,
        validationList: List<ValidationState>
    )

    fun setErrors(context: Context, validationList: List<ValidationState>, layoutList: List<TextInputLayout>) {
        validationList.forEach { element ->
            when (element) {
                is ValidationState.Success -> {}

                is ValidationState.Error -> {
                    layoutList[validationList.indexOf(element)].error = context.getString(element.messageStringId)
                }

                is ValidationState.Inactive -> {}
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setOnClickListeners(layoutList: List<TextInputLayout>){
        layoutList.forEach {
            it.editText!!.setOnTouchListener { _, _ ->
                it.isErrorEnabled = false
                false
            }
        }
    }
}