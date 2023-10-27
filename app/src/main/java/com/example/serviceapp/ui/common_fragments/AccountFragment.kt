package com.example.serviceapp.ui.common_fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.databinding.AccountFragmentBinding
import com.example.serviceapp.ui.dialogs.DeleteDialog
import com.example.serviceapp.ui.dialogs.EmailDialog
import com.example.serviceapp.ui.dialogs.NewPassDialog
import com.example.serviceapp.ui.dialogs.UsernameDialog
import com.example.serviceapp.domain.view_models.MainViewModel
import com.example.serviceapp.domain.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.utils.DialogType
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.models.SignUpState
import com.example.serviceapp.domain.view_models.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.account_fragment) {
    private val binding by viewBinding(AccountFragmentBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    private val usernameDialog by lazy {
        UsernameDialog(requireContext(), binding, firebaseViewModel.firebaseAuth)
    }

    private val emailDialog by lazy {
        EmailDialog(requireContext(), binding, firebaseViewModel.firebaseAuth)
    }

    private val newPassDialog by lazy {
        NewPassDialog(requireContext(), binding, firebaseViewModel.firebaseAuth)
    }

    private val deleteDialog by lazy {
        DeleteDialog(requireContext(), binding, firebaseViewModel.firebaseAuth)
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
            changePasswordView.setOnClickListener {
                accountViewModel.setupDialog(
                    requireContext(),
                    LayoutInflater.from(context).inflate(R.layout.new_password_dialog, null),
                    DialogType.PASSWORD,
                    newPassDialog
                )
            }
            exitButton.setOnClickListener {
                firebaseViewModel.updateSignInState(SignInState.UnsignedIn)
                mainViewModel.navigate(fragment = R.id.login_fragment)
            }
            deleteButton.setOnClickListener {
                firebaseViewModel.updateSignUpState(SignUpState.UnsignedUp)
                firebaseViewModel.updateSignInState(SignInState.UnsignedIn)
                accountViewModel.setupDialog(
                    requireContext(),
                    LayoutInflater.from(context).inflate(R.layout.delete_account_dialog, null),
                    DialogType.DELETE,
                    deleteDialog
                )
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
        }
    }

    private fun initFragment() {
        with(binding) {
            accountViewModel.setupUserAvatarDir(requireContext())
            initContracts()
            with(firebaseViewModel) {
                changeUsernameView.text = firebaseAuth.currentUser!!.displayName
                changeEmailView.text = firebaseAuth.currentUser!!.email
                phoneNumberView.text = firebaseAuth.currentUser!!.phoneNumber
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
            override fun createIntent(context: Context, input: List<Uri>): Intent = accountViewModel.getUCropIntent(context, input)

            override fun parseResult(resultCode: Int, intent: Intent?): Uri = accountViewModel.getUri(resultCode, intent)
        }

        cropImage = registerForActivityResult(uCropContract) { uri ->
            with(accountViewModel){
                if (prevUserAvatarFile.exists()){
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