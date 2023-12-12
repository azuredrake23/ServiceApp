package com.example.serviceapp.data.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.serviceapp.data.R

object DataExtensions {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun createWarningAlertDialog(context: Context, message: String){
        AlertDialog.Builder(context).setIcon(R.drawable.warning).setMessage(message).show()
    }
}