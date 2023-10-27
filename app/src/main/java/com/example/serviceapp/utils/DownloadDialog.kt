package com.example.serviceapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import com.example.serviceapp.R
import com.example.serviceapp.ui.main.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

object DownloadDialog {
    private var downloadDialog: Dialog? = null

    fun showDownloadDialog(fragmentActivity: FragmentActivity) {
        if (downloadDialog == null) {
            downloadDialog = Dialog(fragmentActivity).apply {
                setCancelable(false)
                setContentView(R.layout.progress_bar)
                window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
        downloadDialog!!.show()
    }

    fun cancelDownloadDialog() {
        if (downloadDialog != null)
            downloadDialog!!.dismiss()
    }
}