package com.example.serviceapp.domain.view_models

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.ResourceManager
import com.example.serviceapp.domain.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.ui.dialogs.Dialog
import com.example.serviceapp.utils.DialogType
import com.google.android.material.textfield.TextInputLayout
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val firebaseViewModel: FirebaseViewModel,
    private val resourceManager: ResourceManager,
) : ViewModel() {

    val imageLiveData = MutableLiveData<Bitmap>()

    val uriLiveData = MutableLiveData<Uri>()

    private lateinit var userAvatarDir: File
    lateinit var userAvatarFile: File
    lateinit var prevUserAvatarFile: File

    fun resetFiles() {
        userAvatarFile.delete()
        prevUserAvatarFile.renameTo(File(userAvatarFile.absolutePath))
    }

    fun setupReserveFile() {
        prevUserAvatarFile = File(userAvatarDir, "prevUserAvatar.jpg")
        if (userAvatarFile.exists())
            userAvatarFile.copyTo(prevUserAvatarFile, true)
    }

    fun setupUserAvatarDir(context: Context) {
        userAvatarDir = File(context.filesDir, "userAvatar")
        userAvatarFile = File(userAvatarDir, "userAvatar.jpg")

        if (userAvatarFile.exists()) {
            downloadUserAvatar(context, userAvatarFile.toUri())
        } else {
            userAvatarDir.mkdirs()
        }
    }

    fun downloadUserAvatar(context: Context, uri: Uri) {
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .apply(RequestOptions.circleCropTransform())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    imageLiveData.value = resource
                    uriLiveData.value = uri
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    fun getUCropIntent(context: Context, input: List<Uri>): Intent {
        val inputUri = input[0]
        val outputUri = input[1]
        val uCropOptions = UCrop.Options()
        uCropOptions.setCircleDimmedLayer(true)
        val uCrop = UCrop.of(inputUri, outputUri)
            .withAspectRatio(1F, 1F)
            .withOptions(uCropOptions)
        return uCrop.getIntent(context)
    }

    fun getUri(resultCode: Int, intent: Intent?): Uri {
        if (resultCode == Activity.RESULT_OK) {
            try {
                prevUserAvatarFile.delete()
                return UCrop.getOutput(intent!!)!!
            } catch (_: Exception) {
            }
        }
        return prevUserAvatarFile.toUri()
    }

    fun setupDialog(
        context: Context,
        dialogView: View,
        dialogType: DialogType,
        customDialog: Dialog
    ) {
        val dialog = AlertDialog.Builder(context).setPositiveButton("OK", null)
            .setNegativeButton(resourceManager.getString(R.string.cancel), null).setView(dialogView)
            .show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//        builder.setTitle(getString(R.string.confirm_data))
        positiveButton.setOnClickListener {
            setDialogPositiveButtonListener(
                dialogView,
                dialogType,
                dialog,
                customDialog
            )
        }
        negativeButton.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun setDialogPositiveButtonListener(
        view: View,
        dialogType: DialogType,
        dialog: AlertDialog,
        customDialog: Dialog
    ) {
        when (dialogType) {
            DialogType.USERNAME -> {
                setDialog(
                    view.findViewById(R.id.firstUsernameLayout),
                    view.findViewById(R.id.secondUsernameLayout),
                    dialog,
                    customDialog,
                    dialogType
                )
            }

            DialogType.EMAIL -> {
                setDialog(
                    view.findViewById(R.id.firstEmailLayout),
                    view.findViewById(R.id.secondEmailLayout),
                    dialog,
                    customDialog,
                    dialogType
                )
            }

            DialogType.PASSWORD -> {
                setDialog(
                    view.findViewById(R.id.firstUpdatePassLayout),
                    view.findViewById(R.id.secondUpdatePassLayout),
                    dialog,
                    customDialog,
                    dialogType
                )
            }

            DialogType.DELETE -> {
                setDialog(
                    view.findViewById(R.id.firstDeleteLayout),
                    null,
                    dialog,
                    customDialog,
                    dialogType
                )
            }
        }
    }

    private fun setDialog(
        view1: TextInputLayout,
        view2: TextInputLayout?,
        dialog: AlertDialog,
        customDialog: Dialog,
        dialogType: DialogType
    ) {
        val layoutList: List<TextInputLayout> = if (view2 != null) {
            listOf(view1, view2)
        } else listOf(view1)
        val validationList = firebaseViewModel.validateFields(dialogType, layoutList)
        customDialog.setPositiveClickListener(
            dialog, dialogType, layoutList, validationList
        )
    }

}