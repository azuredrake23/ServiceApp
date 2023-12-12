package com.example.serviceapp.ui.fragments.firebase_fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.utils.DataExtensions.createWarningAlertDialog
import com.example.serviceapp.databinding.LoginFragmentBinding
import com.example.serviceapp.ui.view_models.FirebaseViewModel
import com.example.serviceapp.ui.view_models.MainViewModel
import com.google.android.play.integrity.internal.c
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {

    private val binding by viewBinding(LoginFragmentBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        signUpGoogleCheck()
        setListeners()
        setObservers()
    }

    private fun signUpGoogleCheck() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                firebaseViewModel.googleSignUpActivityResult(it)
            }
        }
    }

    private fun setObservers() {
        with(mainViewModel) {
            navigateFragmentValue.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                findNavController().navigate(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            downloadDialogState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                checkDownloadDialogState(requireActivity(), it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            warningDialogMessage.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                createWarningAlertDialog(requireActivity(), it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun setListeners() {
        with(firebaseViewModel) {
            with(binding) {
                googleButton.setOnClickListener {
                    updateSignInState(SignInState.Google)
                    signIn(requireActivity(), activityResultLauncher)
                }
                phoneNumberButton.setOnClickListener {
                    updateSignInState(SignInState.PhoneNumber)
                    mainViewModel.navigate(R.id.phone_number_fragment)
                }
            }
        }
    }


}
