package com.example.serviceapp.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.FragmentActivity
import com.example.serviceapp.R

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