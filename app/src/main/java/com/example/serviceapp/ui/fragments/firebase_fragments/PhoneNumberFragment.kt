package com.example.serviceapp.ui.fragments.firebase_fragments

import android.content.Context
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
import com.example.serviceapp.data.utils.DataExtensions
import com.example.serviceapp.data.utils.DataExtensions.createWarningAlertDialog
import com.example.serviceapp.data.utils.PreferenceManager
import com.example.serviceapp.databinding.PhoneNumberFragmentBinding
import com.example.serviceapp.ui.view_models.FirebaseViewModel
import com.example.serviceapp.ui.view_models.MainViewModel
import com.example.serviceapp.utils.TimerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PhoneNumberFragment : Fragment(R.layout.phone_number_fragment) {
    private val binding by viewBinding(PhoneNumberFragmentBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

    private lateinit var preferenceManager: PreferenceManager

    private var countryCode = ""
    private lateinit var fragmentPhoneNumber: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferenceManager = PreferenceManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
        setObservers()
    }

    private fun setObservers() {
        with(mainViewModel) {
            navigateFragmentValue.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).onEach {
                findNavController().navigate(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            downloadDialogState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).onEach {
                checkDownloadDialogState(requireActivity(), it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            warningDialogMessage.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).onEach {
                createWarningAlertDialog(requireActivity(), it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
        with(firebaseViewModel) {
            phoneNumber.observe(viewLifecycleOwner) {
                fragmentPhoneNumber = it
            }
//            timerValue.observe(viewLifecycleOwner) {
//                when (it) {
//                    is TimerState.Processing -> {
//                        if (it.leftTime != null)
//                            mainViewModel.warningDialog(
//                                getString(R.string.get_otp_later_message) + " ${it.leftTime} " + getString(
//                                    R.string.seconds
//                                )
//                            )
//                    }
//
//                    is TimerState.Stopped -> {
//                    }
//
//                }
//            }
        }
    }

    private fun initFragment() {
        with(binding) {
            countryCode =
                preferenceManager.get(resources.getString(R.string.country_code), "+91")
            ccp.setOnCountryChangeListener {
                countryCode = ccp.selectedCountryCode.toString()
                preferenceManager.put(getString(R.string.country_code), ccp.selectedCountryCode)
            }
            continueButton.setOnClickListener {
                with(firebaseViewModel) {
                    if (editTextPhone.text.isNotEmpty()) {
                        if (editTextPhone.text.length == 9) {
                            fragmentPhoneNumber =
                                "+" + ccp.selectedCountryCode.toString() + editTextPhone.text.toString()
                            if (timerValue.value is TimerState.Stopped)
                                verifyPhoneNumber(
                                    requireActivity(),
                                    fragmentPhoneNumber,
                                    isResend = false,
                                    token = null
                                )
                            else {
                                mainViewModel.warningDialog(
                                    getString(R.string.get_otp_later_message) + " ${(timerValue.value as TimerState.Processing).leftTime} " + getString(
                                        R.string.seconds
                                    )
                                )
                            }
//                            isUserExists(requireActivity(), fragmentPhoneNumber, false, null)
                        } else {
                            editTextPhone.error = getString(R.string.correct_phone_number_message)
                        }
                    } else {
                        editTextPhone.error = getString(R.string.enter_value_message)
                    }
                }
            }
        }
    }
}