package com.example.serviceapp.data.entities

import android.annotation.SuppressLint
import com.example.serviceapp.R

object Errors {
    @SuppressLint("NonConstantResourceId")
    const val SIGNED_IN: Int = R.string.sign_in_success_message //no problems/errors
    @SuppressLint("NonConstantResourceId")
    const val EMPTY_FIELDS: Int = R.string.empty_fields_message
    @SuppressLint("NonConstantResourceId")
    const val SIGNED_IN_INCORRECT_PASS: Int = R.string.sign_in_incorrect_pass_message
    @SuppressLint("NonConstantResourceId")
    const val SIGNED_IN_USER_NOT_FOUND: Int = R.string.sign_in_user_not_found_message
}