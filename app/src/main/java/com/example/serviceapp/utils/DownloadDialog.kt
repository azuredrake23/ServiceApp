package com.example.serviceapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.serviceapp.R
import javax.inject.Singleton

@Singleton
class DownloadDialog(context: Context) {
    private val downloadDialog: Dialog

    init {
        downloadDialog = Dialog(context)
        downloadDialog.setCancelable(false)
        downloadDialog.setContentView(R.layout.progress_bar)
        downloadDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun showDownloadDialog() {
//        val progressBar =
//            ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
//        val params = RelativeLayout.LayoutParams(100, 100)
//        params.addRule(RelativeLayout.CENTER_IN_PARENT)
//        requireActivity().findViewById<ConstraintLayout>(R.id.login_layout).addView(progressBar, params)
//        requireActivity().window.setFlags(
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//        )
        downloadDialog.show()
    }

    fun cancelDownloadDialog(){
        downloadDialog.dismiss()
    }
}