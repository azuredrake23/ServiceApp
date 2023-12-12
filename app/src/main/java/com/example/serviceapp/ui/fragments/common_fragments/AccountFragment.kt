package com.example.serviceapp.ui.fragments.common_fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.models.SignUpState
import com.example.serviceapp.databinding.AccountFragmentBinding
import com.example.serviceapp.ui.dialogs.Dialog
import com.example.serviceapp.ui.view_models.AccountViewModel
import com.example.serviceapp.ui.view_models.FirebaseViewModel
import com.example.serviceapp.ui.view_models.MainViewModel
import com.example.serviceapp.utils.DialogType
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.account_fragment) {
    private val binding by viewBinding(AccountFragmentBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    private val usernameDialog by lazy {
        Dialog(requireContext(), binding, mainViewModel, firebaseViewModel)
    }

    private val emailDialog by lazy {
        Dialog(requireContext(), binding, mainViewModel, firebaseViewModel)
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var uCropContract: ActivityResultContract<List<Uri>, Uri>
    private lateinit var cropImage: ActivityResultLauncher<List<Uri>>

    private var lastTimeClick = 0L
    private var debounceDelay: Long = 1000L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        with(binding) {
            changeUsernameView.setOnClickListener {
                accountViewModel.setupDialog(
                    requireContext(),
                    LayoutInflater.from(context).inflate(R.layout.username_dialog, null),
                    DialogType.USERNAME,
                    usernameDialog
                )
            }
            changeEmailView.setOnClickListener {
                accountViewModel.setupDialog(
                    requireContext(),
                    LayoutInflater.from(context).inflate(R.layout.email_dialog, null),
                    DialogType.EMAIL,
                    emailDialog
                )
            }
            exitButton.setOnClickListener {
                firebaseViewModel.updateSignInState(SignInState.UnsignedIn)
                firebaseViewModel.firebaseAuth.signOut()
                mainViewModel.navigate(R.id.login_fragment)
            }
            deleteButton.setOnClickListener {
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setTitle(com.example.serviceapp.R.string.account_deleted_confirmation_message)
                    .setNegativeButton(getString(com.example.serviceapp.R.string.cancel), null)
                    .setPositiveButton(getString(R.string.yes), null)
                    .show()
                val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

                positiveButton.setOnClickListener {
                    with(firebaseViewModel) {
                        val uidOfDeletedUser = firebaseAuth.currentUser!!.uid
                        firebaseAuth.currentUser!!.delete()
                            .addOnCompleteListener { delete ->
                                if (delete.isSuccessful) {
                                    alertDialog.dismiss()
                                    firebaseViewModel.firebaseRealtimeDatabaseUserReference.child(
                                        uidOfDeletedUser
                                    ).removeValue()
                                    firebaseAuth.signOut()
                                    mainViewModel.navigate(R.id.login_fragment)
                                    mainViewModel.popupMessage(requireContext().getString(R.string.account_deleted_message))
                                }
                            }
                    }
                }

                negativeButton.setOnClickListener {
                    alertDialog.cancel()
                }

                firebaseViewModel.updateSignInState(SignInState.UnsignedIn)
            }
            photoFL.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTimeClick > debounceDelay) {
                    lastTimeClick = currentTime
                    accountViewModel.setupReserveFile()
                    startMediaPermissionRequest()
                }
            }
        }
    }

    private fun setObservers() {
        with(accountViewModel) {
            imageLiveData.observe(viewLifecycleOwner) {
                binding.profileImage.setImageBitmap(it)
            }
            uriLiveData.observe(viewLifecycleOwner) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(it)
                    .build()
                firebaseViewModel.firebaseAuth.currentUser!!.updateProfile(profileUpdates)
            }
        }
        with(mainViewModel) {
            navigateFragmentValue.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).onEach {
                findNavController().navigate(
                    it
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun initFragment() {
        with(binding) {
            accountViewModel.setupUserAvatarDir(requireContext())
            initContracts()
            with(firebaseViewModel) {
//                firebaseRealtimeDatabaseUserReference.child(firebaseViewModel.firebaseAuth.currentUser!!.uid)
//                    .addListenerForSingleValueEvent(
//                        object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                snapshot.getValue<User>()?.let {
//                                    changeUsernameView.text = it.displayName
//                                    changeEmailView.text = it.email
//                                    phoneNumberView.text = it.phoneNumber
//                                }
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//
//                            }
//                        })
                val listOfProviderData = firebaseAuth.currentUser!!.providerData
                var counter = 0
                if (listOfProviderData.isEmpty()){
                    updateSignInState(SignInState.UnsignedIn)
                }
                listOfProviderData.forEach {
                    if (it.providerId == "google.com") {
                        counter++
                        updateSignInState(SignInState.Google)
                    }
                    if (it.providerId == "phone"){
                        counter++
                        updateSignInState(SignInState.PhoneNumber)
                    }
                }
                if (counter == 2){
                    updateSignInState(SignInState.GoogleAndPhoneNumber)
                    counter = 0
                }
                when (signInState.value) {
                    SignInState.Google -> {
                        changeUsernameView.text = firebaseAuth.currentUser!!.displayName
                        changeEmailView.text = firebaseAuth.currentUser!!.email
                        phoneNumberView.setOnClickListener {
                            mainViewModel.navigate(R.id.phone_number_fragment)
                        }
                    }

                    SignInState.PhoneNumber -> {
                        changeUsernameView.isEnabled = false
                        changeEmailView.isEnabled = false
//                        changeUsernameView.text = getString(R.string.)
//                        changeEmailView.text = getString(R.string.)
                        phoneNumberView.text = firebaseAuth.currentUser!!.phoneNumber
                    }

                    SignInState.GoogleAndPhoneNumber -> {
                        changeUsernameView.text = firebaseAuth.currentUser!!.displayName
                        phoneNumberView.text = firebaseAuth.currentUser!!.phoneNumber
                        changeEmailView.text = firebaseAuth.currentUser!!.email
                    }

                    SignInState.UnsignedIn -> {}
                }
            }
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
            with(accountViewModel) {
                if (uri != null) {
                    cropImage.launch(listOf(uri, userAvatarFile.toUri()))
                }
            }
        }

        uCropContract = object : ActivityResultContract<List<Uri>, Uri>() {
            override fun createIntent(context: Context, input: List<Uri>): Intent =
                accountViewModel.getUCropIntent(context, input)

            override fun parseResult(resultCode: Int, intent: Intent?): Uri =
                accountViewModel.getUri(resultCode, intent)
        }

        cropImage = registerForActivityResult(uCropContract) { uri ->
            with(accountViewModel) {
                if (prevUserAvatarFile.exists()) {
                    resetFiles()
                }
                downloadUserAvatar(requireContext(), uri)
            }
        }
    }

    private fun startMediaPermissionRequest() {
        if (Build.VERSION.SDK_INT <= 32) {
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
        } else if (Build.VERSION.SDK_INT > 32) {
            requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
        }
    }
}