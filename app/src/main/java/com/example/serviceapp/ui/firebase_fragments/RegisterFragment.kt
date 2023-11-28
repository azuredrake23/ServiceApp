package com.example.serviceapp.ui.firebase_fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.createWarningAlertDialog
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.data.models.DownloadDialogState
import com.example.serviceapp.databinding.RegisterFragmentBinding
import com.example.serviceapp.domain.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.data.models.SignInState
import com.example.serviceapp.data.models.SignUpState
import com.example.serviceapp.data.models.ValidationState
import com.example.serviceapp.domain.view_models.MainViewModel
import com.google.firebase.auth.ktx.userProfileChangeRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.register_fragment) {
    val binding by viewBinding(RegisterFragmentBinding::bind)

    private val mainViewModel: MainViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObservers()
    }

    private fun setObservers() {
        with(mainViewModel) {
            navigateFragmentValue.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach {
                findNavController().navigate(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            popupValue.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach {
                showToast(requireContext(), it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            downloadDialogState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                checkDownloadDialogState(requireActivity(), it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            warningDialogMessage.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).onEach {
                createWarningAlertDialog(requireContext(), it)
            }
        }
    }

    private fun initView() {
        firebaseViewModel.initRegisterFragmentView(requireContext(), binding)
    }

}