package com.example.serviceapp.ui.common_fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.AccountFragmentBinding
import com.example.serviceapp.ui.common_fragments.models.ValidationState
import com.example.serviceapp.ui.view_models.MainViewModel
import com.example.serviceapp.ui.view_models.database_view_models.UserDatabaseViewModel
import com.example.serviceapp.ui.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.utils.DialogType
import com.example.serviceapp.utils.SignInState
import com.example.serviceapp.utils.SignUpState
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File


@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.account_fragment) {
    private val binding by viewBinding(AccountFragmentBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserDatabaseViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

    private val firebaseAuth by lazy {
        firebaseViewModel.firebaseAuth
    }

    private val firebaseRealtimeDatabaseUserRef by lazy {
        firebaseViewModel.firebaseRealtimeDatabaseUserRef
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var uCropContract: ActivityResultContract<List<Uri>, Uri>
    private lateinit var cropImage: ActivityResultLauncher<List<Uri>>
    private lateinit var userAvatarDir: File
    private lateinit var userAvatarFile: File
    private lateinit var dialogView: View
    private lateinit var validationList: List<ValidationState>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
        setObservers()
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        initUserAvatarDir()
    }

    private fun setListeners() {
        with(binding) {
            changeUsernameView.setOnClickListener {
                dialogView =
                    LayoutInflater.from(context).inflate(R.layout.username_dialog, null)
                dialogVerification(
                    DialogType.USERNAME,
                    dialogView
                )
            }
            changeEmailView.setOnClickListener {
                dialogView =
                    LayoutInflater.from(context).inflate(R.layout.email_dialog, null)
                dialogVerification(
                    DialogType.EMAIL,
                    dialogView
                )
            }
            changePasswordView.setOnClickListener {
                dialogView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.new_password_dialog, null)
                dialogVerification(
                    DialogType.PASSWORD,
                    dialogView
                )
            }
            exitButton.setOnClickListener {
                firebaseViewModel.updateSignInState(SignInState.UnsignedIn)
                findNavController().navigate(R.id.login_fragment)
            }
            deleteButton.setOnClickListener {
                firebaseViewModel.updateSignUpState(SignUpState.UnsignedUp)
                firebaseViewModel.updateSignInState(SignInState.UnsignedIn)
                dialogView =
                    LayoutInflater.from(context).inflate(R.layout.password_dialog, null)
                dialogVerification(
                    DialogType.DELETE,
                    dialogView
                )
            }
            photoFL.setOnClickListener {
                createUserAvatar()
                startMediaPermissionRequest()
            }
        }
    }

    private fun dialogVerification(
        dialogType: DialogType,
        view: View
    ) {
        val dialog = AlertDialog.Builder(context).setPositiveButton("OK", null)
            .setNegativeButton(getString(R.string.cancel), null).setView(view).show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//        builder.setTitle(getString(R.string.confirm_data))
        positiveButton.setOnClickListener {
            dialogPositiveButtonListener(dialog, dialogType, view)
        }
        negativeButton.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun dialogPositiveButtonListener(
        dialog: DialogInterface,
        dialogType: DialogType,
        view: View
    ) {
        when (dialogType) {
            DialogType.USERNAME -> {
                val username = view.findViewById<EditText>(R.id.username)
                val password =
                    view.findViewById<EditText>(R.id.usernamePassword)
                initEditTextListeners(username, password)
                val usernameText = username.text.toString()
                val passwordText = password.text.toString()
                mainViewModel.validateFields(listOf(usernameText, passwordText))
                validationList.forEach {
                    when (it){
                        is ValidationState.Success -> {it.text} // что-то сделать с полями и/или изменить лист, чтобы он соответсвовал всем полям и работал корректно
                        is ValidationState.Error -> {it.messageStringId}
                        is ValidationState.Inactive -> {it}
                    }
                }
                when (validationList) {

                }
                if (usernameText.isNotEmpty() && passwordText.isNotEmpty()) {
                    val credential = EmailAuthProvider
                        .getCredential(
                            binding.changeEmailView.text.toString(),
                            passwordText
                        )
                    firebaseAuth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                dialog.dismiss()
                                binding.changeUsernameView.text = usernameText
                                firebaseAuth.currentUser!!.updateProfile(
                                    userProfileChangeRequest {
                                        displayName = usernameText
                                    })
                                showToast(
                                    requireContext(),
                                    getString(R.string.username_changed_message)
                                )
                            } else {
                                password.error = getString(R.string.incorrect_pass_message)
                            }
                        }
                }

                if (usernameText.isNotEmpty() && passwordText.isEmpty()) {
                    password.error = "This field is empty"
                }

                if (usernameText.isEmpty() && passwordText.isNotEmpty()) {
                    username.error = "This field is empty"
                }

                if (usernameText.isEmpty() && passwordText.isEmpty()) {
                    password.error = "This field is empty"
                    username.error = "This field is empty"
                }
            }

            DialogType.EMAIL -> {
                val email = view.findViewById<EditText>(R.id.textAccessEmail)
                val password = view.findViewById<EditText>(R.id.textAccessPassword)
                initEditTextListeners(email, password)
                val emailText = email.text.toString()
                val passwordText = password.text.toString()
                if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                    val credential = EmailAuthProvider
                        .getCredential(
                            binding.changeEmailView.text.toString(),
                            passwordText
                        )
                    firebaseAuth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                dialog.dismiss()
                                binding.changeEmailView.text = emailText
                                firebaseAuth.currentUser!!.updateEmail(emailText)
                                showToast(
                                    requireContext(),
                                    getString(R.string.email_changed_message)
                                )
                            } else {
                                showToast(
                                    requireContext(),
                                    getString(R.string.enter_correct_data_message)
                                )
                            }
                        }
                } else {
                    setWrongData(email, password)
                    showToast(
                        requireContext(),
                        getString(R.string.enter_value_message)
                    )
                }
            }

            DialogType.PASSWORD -> {
                val updatePassword =
                    view.findViewById<EditText>(R.id.updatePassword)
                val updateNewPassword =
                    view.findViewById<EditText>(R.id.updateNewPassword)
                initEditTextListeners(updatePassword, updateNewPassword)
                val updatePasswordText =
                    updatePassword.text.toString()
                val updateNewPasswordText =
                    updateNewPassword.text.toString()
                if (updatePasswordText.isNotEmpty() && updateNewPasswordText.isNotEmpty()) {
                    val credential = EmailAuthProvider
                        .getCredential(
                            binding.changeEmailView.text.toString(),
                            updatePasswordText
                        )
                    firebaseAuth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                dialog.dismiss()
                                firebaseAuth.currentUser!!.updatePassword(
                                    updateNewPasswordText
                                )
                                showToast(
                                    requireContext(),
                                    getString(R.string.password_changed_message)
                                )
                            } else {
                                showToast(
                                    requireContext(),
                                    getString(R.string.enter_correct_data_message)
                                )
                            }
                        }
                } else {
                    setWrongData(updatePassword, updateNewPassword)
                    showToast(
                        requireContext(),
                        getString(R.string.enter_value_message)
                    )
                }
            }

            DialogType.DELETE -> {
                val confirmPassword =
                    view.findViewById<EditText>(R.id.confirmPassword)
                initEditTextListeners(view1 = confirmPassword)
                val confirmPasswordText =
                    confirmPassword.text.toString()
                if (confirmPasswordText.isNotEmpty()) {
                    val credential = EmailAuthProvider
                        .getCredential(
                            binding.changeEmailView.text.toString(),
                            confirmPasswordText
                        )
                    firebaseAuth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener { it ->
                            if (it.isSuccessful) {
//                                firebaseAuth.signOut()
                                firebaseRealtimeDatabaseUserRef
                                    .equalTo(firebaseAuth.currentUser!!.uid)
                                    .addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            dataSnapshot.ref.removeValue()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                                firebaseAuth.currentUser!!.delete()
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            dialog.dismiss()
                                            firebaseAuth.signOut()
                                            findNavController().navigate(R.id.login_fragment)
                                            showToast(
                                                requireContext(),
                                                getString(R.string.account_deleted_message)
                                            )
                                        }
                                    }
                            } else {
                                showToast(
                                    requireContext(),
                                    getString(R.string.enter_correct_data_message)
                                )
                            }
                        }
                } else {
                    setWrongData(view1 = confirmPassword)
                    showToast(
                        requireContext(),
                        getString(R.string.enter_value_message)
                    )
                }
            }
        }
    }

    private fun initEditTextListeners(view1: EditText, view2: EditText? = null) {
        view1.setOnClickListener {
            it.setBackgroundResource(R.drawable.normal_borders_text)
        }
        view2?.setOnClickListener {
            it.setBackgroundResource(R.drawable.normal_borders_text)
        }
    }

    private fun setWrongData(view1: View, view2: View? = null) {
        view1.setBackgroundResource(R.drawable.red_borders_text)
        view2?.setBackgroundResource(R.drawable.red_borders_text)
    }

    private fun setObservers() {
        with(userViewModel) {

        }
        with(mainViewModel) {
            listDialogFieldsState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).onEach {
                validationList = it
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun initFragment() {
        with(binding) {
            initContracts()
            changeUsernameView.text = firebaseAuth.currentUser!!.displayName
            changeEmailView.text = firebaseAuth.currentUser!!.email
            phoneNumber.text = firebaseAuth.currentUser!!.phoneNumber
        }
    }

    private fun initContracts() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getContent.launch("image/*")
            }
        }

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val inputUri: Uri = uri
                cropImage.launch(listOf(inputUri, userAvatarFile.toUri()))
            }
        }

        uCropContract = object : ActivityResultContract<List<Uri>, Uri>() {
            override fun createIntent(context: Context, input: List<Uri>): Intent {
                val inputUri = input[0]
                val outputUri = input[1]
                val uCropOptions = UCrop.Options()
                uCropOptions.setCircleDimmedLayer(true)
                val uCrop = UCrop.of(inputUri, outputUri)
                    .withAspectRatio(1F, 1F)
                    .withOptions(uCropOptions)
                return uCrop.getIntent(requireContext())
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Uri {
                return UCrop.getOutput(intent!!)!!
            }
        }

        cropImage = registerForActivityResult(uCropContract) { uri ->
            downloadUserAvatar(uri)
        }
    }

    private fun initUserAvatarDir() {
        userAvatarDir = File(requireContext().filesDir, "userAvatar")
        if (userAvatarDir.exists()) {
            downloadUserAvatar(userAvatarDir.listFiles()?.get(0)!!.toUri())
        } else {
            userAvatarDir.mkdirs()
        }
    }

    private fun downloadUserAvatar(uri: Uri) {
        Glide.with(requireContext())
            .asBitmap()
            .load(uri)
            .apply(RequestOptions.circleCropTransform())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.profileImage.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })
    }

    private fun createUserAvatar() {
        if (userAvatarDir.listFiles()?.isNotEmpty() == true) {
            userAvatarDir.listFiles()?.get(0)!!.delete()
        }
        userAvatarFile = File(userAvatarDir, "${System.currentTimeMillis()}.jpg")
        userAvatarFile.createNewFile()
    }

    private fun startMediaPermissionRequest() {
        if (Build.VERSION.SDK_INT <= 32) {
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
        } else if (Build.VERSION.SDK_INT > 32) {
            requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
        }
    }
}