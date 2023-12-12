package com.example.serviceapp.utils

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.serviceapp.R
import com.example.serviceapp.data.models.User
import com.example.serviceapp.ui.view_models.FirebaseViewModel


fun Int?.isInRange(min: Int, max: Int): Boolean = this?.let { it in min..max } ?: false

//fun View.showKeyboard() {
//    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
////    val imgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
////    imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
//}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun setUserToRealtimeDatabase(
    firebaseViewModel: FirebaseViewModel,
    photo: String?,
    username: String?,
    email: String?,
    password: String?
) {
    firebaseViewModel.firebaseRealtimeDatabaseUserReference.child(firebaseViewModel.firebaseAuth.currentUser!!.uid)
        .setValue(
            User(photo, username, email, password)
        )
}