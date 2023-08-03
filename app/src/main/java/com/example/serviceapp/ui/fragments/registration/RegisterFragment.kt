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
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.entities.Errors
import com.example.serviceapp.databinding.RegisterFragmentBinding
import com.example.serviceapp.ui.fragments.database_view_models.UserDatabaseViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterFragment: Fragment(R.layout.register_fragment) {

    private val binding by viewBinding(RegisterFragmentBinding:: bind)
    private val userViewModel: UserDatabaseViewModel by activityViewModels()

    var users = listOf<User>()

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
//        userViewModel.insert(User(0,"", "", binding.textLogin.text.toString(), ""))
    }

    private fun setObservers() {
        with(userViewModel){
            allUsers
                .flowWithLifecycle(
                    viewLifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED
                )
                .onEach {
                    users = it
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun setListeners() {
        onRegistration()
    }

    private fun onRegistration() {
        with(binding){
            signUpButton.setOnClickListener {
                if (nameReg.text.isEmpty() || emailReg.text.isEmpty() || passReg.text.isEmpty() || confirmPassReg.text.isEmpty()){
                    Snackbar.make(
                        binding.root,
                        Errors.EMPTY_FIELDS,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                } else {
                    userViewModel.insert(
                        User(
                            name = nameReg.text.toString(),
                            email = emailReg.text.toString(),
                            password = passReg.text.toString()
                        )
                    )
                    Snackbar.make(
                        binding.root,
                        R.string.sign_up_success_message,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
//                    Snackbar.make(
//                        binding.root,
//                        users.toString(),
//                        Snackbar.LENGTH_SHORT
//                    )
//                        .show()
                    findNavController().navigate(R.id.main_fragment)
                }
                //сделать корректную проверку мейла по маске

            }
        }
    }

}