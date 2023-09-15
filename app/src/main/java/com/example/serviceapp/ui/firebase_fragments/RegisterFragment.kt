package com.example.serviceapp.ui.firebase_fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.databinding.RegisterFragmentBinding
import com.example.serviceapp.ui.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.utils.SignInState
import com.example.serviceapp.utils.SignUpState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.userProfileChangeRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.register_fragment) {
    val binding by viewBinding(RegisterFragmentBinding::bind)
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

    private val firebaseAuth by lazy {
        firebaseViewModel.firebaseAuth
    }
    private val firebaseUserReference by lazy {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        when (firebaseViewModel.getSignInState()){
            SignInState.PhoneNumber -> {
                with (binding){
                    continueButton.setOnClickListener {
                        if (nameEnter.text.toString() != "" && emailEnter.text.toString() != "" && passwordEnter.text.toString() != "")
                        {
                            firebaseAuth.currentUser!!.updateProfile(userProfileChangeRequest {
                                displayName = nameEnter.text.toString()
                            })
                            firebaseAuth.currentUser!!.reauthenticate(requireArguments().getParcelable("credentials")!!).addOnCompleteListener {
                                firebaseAuth.currentUser!!.updateEmail(emailEnter.text.toString())
                                firebaseAuth.currentUser!!.updatePassword(passwordEnter.text.toString())
                                val firebaseUser = firebaseAuth.currentUser!!
                                findNavController().navigate(R.id.main_fragment)
                            }
                        }
                    }
                }
            }
            SignInState.Google -> {
                with (binding){
                    nameEnter.isEnabled = false
                    nameEnter.setText(firebaseAuth.currentUser!!.displayName)
                    emailEnter.isEnabled = false
                    emailEnter.setText(firebaseAuth.currentUser!!.email)
                    println("--------------------------------------INIT REGISTER FRAGMENT--------------------------------------")
                    println(firebaseAuth.currentUser!!.providerData[0].providerId)
                    println(firebaseAuth.currentUser!!.providerData[1].providerId)
                    println(firebaseAuth.currentUser!!.providerData[0].email.toString())
                    println(firebaseAuth.currentUser!!.providerData[1].email.toString())
                    println(firebaseAuth.currentUser!!.providerData[0].phoneNumber.toString())
                    println(firebaseAuth.currentUser!!.providerData[1].phoneNumber.toString())
                    println(firebaseAuth.currentUser!!.providerData[0].displayName.toString())
                    println(firebaseAuth.currentUser!!.providerData[1].displayName.toString())
                    println(firebaseAuth.currentUser!!.providerData[0].isEmailVerified.toString())
                    println(firebaseAuth.currentUser!!.providerData[1].isEmailVerified.toString())
                    firebaseViewModel.updateSignUpState(SignUpState.SignedUp)
                    continueButton.setOnClickListener {
                        if (nameEnter.text.toString() != "" && emailEnter.text.toString() != "" && passwordEnter.text.toString() != "")
                        {
                            firebaseAuth.currentUser!!.updatePassword(passwordEnter.text.toString())
                            findNavController().navigate(R.id.main_fragment)
                        }
                    }
                }
            }
            SignInState.UnsignedIn -> {}
        }
    }
}