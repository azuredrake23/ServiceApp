package com.example.serviceapp.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.AccountFragmentBinding
import com.example.serviceapp.ui.fragments.database_view_models.UserDatabaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class AccountFragment : Fragment(R.layout.account_fragment) {

    private val binding by viewBinding(AccountFragmentBinding::bind)
    private val userViewModel: UserDatabaseViewModel by activityViewModels()
    private var firebaseUser: FirebaseUser? = null
    private val firebaseDatabase by lazy {
        Firebase.database
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        with(binding) {
            userName.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    saveButton.visibility = View.VISIBLE
                }
            })
            email.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    saveButton.visibility = View.VISIBLE
                }
            })

//            password.addTextChangedListener(object : TextWatcher {
//
//                override fun afterTextChanged(s: Editable) {}
//
//                override fun beforeTextChanged(
//                    s: CharSequence, start: Int,
//                    count: Int, after: Int
//                ) {
//                }
//
//                override fun onTextChanged(
//                    s: CharSequence, start: Int,
//                    before: Int, count: Int
//                ) {
//                    saveButton.visibility = View.VISIBLE
//                }
//            })
            saveButton.setOnClickListener {
                updateUsername()
                updateUserEmail()
//                updatePassword()
//                currentUser.updatePhoneNumber()
                saveButton.visibility = View.INVISIBLE
            }
            photoFL.setOnClickListener {
                startMediaPermissionRequest()
            }
        }
    }

    private fun setObservers() {
        with(userViewModel) {

        }
    }

    private fun updateUsername() {
        with(binding) {
            if (userName.isFocused) {
                firebaseUser!!.updateProfile(
                    userProfileChangeRequest {
                        displayName = "${userName.text}"
                    })
                userName.clearFocus()
            }
        }
    }

    private fun updateUserEmail() {
        with(binding) {
            if (email.isFocused) {
//                currentUser!!.reauthenticate(
//                    EmailAuthProvider
//                        .getCredentialWithLink(
//                            email.text.toString(),
//                            "123"
//                        )
//                )
//                    .addOnCompleteListener {
//                        currentUser!!.updateEmail(email.text.toString())
//                    }
//                val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
//                currentUser!!.reauthenticate()
                firebaseUser!!.updateEmail(email.text.toString())

                email.clearFocus()
            }
        }
    }

//    private fun updatePassword() {
//        with(binding) {
//            if (password.isFocused) {
//                currentUser!!.reauthenticate(
//                    EmailAuthProvider
//                        .getCredential(
//                            email.text.toString(),
//                            password.text.toString()
//                        )
//                )
//                    .addOnCompleteListener {
//                        currentUser!!.updatePassword(password.text.toString())
//                    }
//                password.clearFocus()
//            }
//        }
//    }

    @SuppressLint("SetTextI18n")
    private fun initFragment() {
        with(binding) {
            firebaseUser = requireArguments().getParcelable("firebaseAuthUser")!!
//            firebaseUser = requireArguments().getParcelable("firebaseUser")
//            firebaseDatabase.reference
            if (firebaseUser != null) {
                if (firebaseUser!!.displayName != "" || firebaseUser!!.displayName != null)
                    userName.setText(firebaseUser!!.displayName)

                if (firebaseUser!!.email != "" || firebaseUser!!.email != null)
                    email.setText(firebaseUser!!.email)
            } else {
                showToast(requireContext(), getString(R.string.user_null_message))
                findNavController().navigate(R.id.login_fragment)
            }
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
                firebaseUser!!.updateProfile(
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