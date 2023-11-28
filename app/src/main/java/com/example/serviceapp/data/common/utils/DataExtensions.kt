package com.example.serviceapp.data.common.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.serviceapp.R

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun createWarningAlertDialog(context: Context, message: String){
    AlertDialog.Builder(context).setIcon(R.drawable.warning).setMessage(message).show()
}












