package com.example.serviceapp.ui.firebase_fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.AccountFragmentBinding
import com.example.serviceapp.ui.view_models.database_view_models.UserDatabaseViewModel
import com.example.serviceapp.ui.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.utils.DialogType
import com.example.serviceapp.utils.SignInState
import com.example.serviceapp.utils.SignUpState
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.account_fragment) {
    private val binding by viewBinding(AccountFragmentBinding::bind)
    private val userViewModel: UserDatabaseViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

    private val firebaseAuth by lazy {
        firebaseViewModel.firebaseAuth
    }
    private val firebaseRealtimeDatabaseUserReference by lazy {
        firebaseViewModel.firebaseRealtimeDatabaseUserReference
    }

    lateinit var dialogView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        with(binding) {
            changeUsernameView.setOnClickListener {
                dialogView = LayoutInflater.from(context).inflate(R.layout.username_dialog, null)
                dialogVerification(
                    DialogType.USERNAME,
                    dialogView
                )
            }
            changeEmailView.setOnClickListener {
                dialogView = LayoutInflater.from(context).inflate(R.layout.email_dialog, null)
                dialogVerification(
                    DialogType.EMAIL,
                    dialogView
                )
            }
            changePasswordView.setOnClickListener {
                dialogView =
                    LayoutInflater.from(context).inflate(R.layout.new_password_dialog, null)
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
                dialogView = LayoutInflater.from(context).inflate(R.layout.password_dialog, null)
                dialogVerification(
                    DialogType.DELETE,
                    dialogView
                )
            }
            photoFL.setOnClickListener {
                startMediaPermissionRequest()
            }
        }
    }

    private fun dialogVerification(
        dialogType: DialogType,
        view: View
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.confirm_data))
        builder.setPositiveButton("OK") { dialog, which ->
            dialogPositiveButtonListener(dialog, dialogType, view)
        }
        builder.setView(view)
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.cancel()
        }
        val dialog = builder.create()
        dialog.show()
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
                if (usernameText.isNotEmpty() && passwordText.isNotEmpty()) {
                    val credential = EmailAuthProvider
                        .getCredential(binding.changeEmailView.text.toString(), passwordText)
                    firebaseAuth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
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
                                showToast(
                                    requireContext(),
                                    getString(R.string.incorrect_pass_message)
                                )
                            }
                        }
                } else {
                    setWrongData(username, password)
                    showToast(
                        requireContext(),
                        getString(R.string.fill_empty_fields_message)
                    )
                }
            }

            DialogType.EMAIL -> {
                val email = view.findViewById<EditText>(R.id.email)
                val password = view.findViewById<EditText>(R.id.emailPassword)
                initEditTextListeners(email, password)
                val emailText = email.text.toString()
                val passwordText = password.text.toString()
                if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                    val credential = EmailAuthProvider
                        .getCredential(binding.changeEmailView.text.toString(), passwordText)
                    firebaseAuth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
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
                    showToast(requireContext(), getString(R.string.fill_empty_fields_message))
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
                        .getCredential(binding.changeEmailView.text.toString(), updatePasswordText)
                    firebaseAuth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                firebaseAuth.currentUser!!.updatePassword(updateNewPasswordText)
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
                    showToast(requireContext(), getString(R.string.fill_empty_fields_message))
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
                        .getCredential(binding.changeEmailView.text.toString(), confirmPasswordText)
                    firebaseAuth.currentUser!!.reauthenticate(credential)
                        .addOnCompleteListener { it ->
                            if (it.isSuccessful) {
//                                firebaseAuth.signOut()
                                firebaseRealtimeDatabaseUserReference
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
                                firebaseAuth.currentUser!!.delete().addOnCompleteListener {
                                    if (it.isSuccessful) {
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
                        getString(R.string.fill_empty_fields_message)
                    )
                }
            }
        }
        dialog.dismiss()
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
    }

    private fun initFragment() {
        with(binding) {
            changeUsernameView.text = firebaseAuth.currentUser!!.displayName
            changeEmailView.text = firebaseAuth.currentUser!!.email
            phoneNumber.text = firebaseAuth.currentUser!!.phoneNumber
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openSomeActivityForResult()
        } else {
            // PERMISSION NOT GRANTED
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), -1)
        }
    }

    private fun startMediaPermissionRequest() {
        if (Build.VERSION.SDK_INT <= 32)
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        else if (Build.VERSION.SDK_INT > 32)
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                firebaseAuth.currentUser!!.updateProfile(
                    userProfileChangeRequest {
                        binding.profileImage.setImageURI(result.data!!.data)
                        photoUri = result.data!!.data
                    })
            }
        }

    private fun openSomeActivityForResult() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }


}