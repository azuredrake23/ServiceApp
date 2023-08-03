package com.example.serviceapp.ui.fragments.registration

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
import com.example.serviceapp.data.entities.Errors
import com.example.serviceapp.databinding.LoginFragmentBinding
import com.example.serviceapp.ui.fragments.database_view_models.UserDatabaseViewModel
import com.example.serviceapp.ui.fragments.models.UserModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginFragment : Fragment(R.layout.login_fragment) {

    private val binding by viewBinding(LoginFragmentBinding::bind)
    private val userViewModel: UserDatabaseViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObservers()
    }

    private fun setObservers() {
        with(userViewModel) {
            userData
                .flowWithLifecycle(
                    viewLifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
                .onEach { answer ->
                    checkUserRegistration(answer)
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun setListeners() {
        signInClickListeners()
    }

    private fun signInClickListeners() {
        with(binding) {
            email.setOnClickListener {
                email.setBackgroundResource(R.drawable.normal_borders_text)
            }
            password.setOnClickListener {
                password.setBackgroundResource(R.drawable.normal_borders_text)
            }
            enterButton.setOnClickListener {
                if (email.text.isEmpty() && password.text.isEmpty()) {
                    checkUserRegistration(UserModel.UserDataWithSignInState(UserModel.UserData(), Errors.EMPTY_FIELDS))
                } else {
                    userViewModel.setUserRegisteredState(
                        email.text.toString(),
                        password.text.toString()
                    )
                }
            }
            signUp.setOnClickListener {
                findNavController().navigate(R.id.register_fragment)
            }
        }
    }

    private fun checkUserRegistration(userDataWithSignInState: UserModel.UserDataWithSignInState) {
        with(binding) {
            when (userDataWithSignInState.errorType) {
                Errors.SIGNED_IN -> {
                    Snackbar.make(
                        binding.root,
                        Errors.SIGNED_IN,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment(userDataWithSignInState.userData.userName, userDataWithSignInState.userData.userEmail, userDataWithSignInState.userData.userPassword, userDataWithSignInState.userData.userState))
                }

                Errors.EMPTY_FIELDS -> {
                    email.setBackgroundResource(R.drawable.red_borders_text)
                    password.setBackgroundResource(R.drawable.red_borders_text)
                    Snackbar.make(
                        binding.root,
                        Errors.EMPTY_FIELDS,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }

                Errors.SIGNED_IN_USER_NOT_FOUND -> {
                    email.setBackgroundResource(R.drawable.red_borders_text)
                    password.setBackgroundResource(R.drawable.normal_borders_text)
                    Snackbar.make(
                        binding.root,
                        Errors.SIGNED_IN_USER_NOT_FOUND,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }

                Errors.SIGNED_IN_INCORRECT_PASS -> {
                    email.setBackgroundResource(R.drawable.normal_borders_text)
                    password.setBackgroundResource(R.drawable.red_borders_text)
                    Snackbar.make(
                        binding.root,
                        Errors.SIGNED_IN_INCORRECT_PASS,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }

                else -> {}
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
