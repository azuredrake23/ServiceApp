package com.example.serviceapp.ui.firebase_fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.LoginFragmentBinding
import com.example.serviceapp.ui.view_models.database_view_models.UserDatabaseViewModel
import com.example.serviceapp.ui.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.utils.SignInState
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {

    private val binding by viewBinding(LoginFragmentBinding::bind)
    private val userViewModel: UserDatabaseViewModel by viewModels()
    private val firebaseViewModel: FirebaseViewModel by viewModels()

    private val firebaseAuth by lazy {
        firebaseViewModel.firebaseAuth
    }
    private val firebaseUser by lazy {
        firebaseViewModel.firebaseUser
    }
    private val oneTapClient by lazy {
        firebaseViewModel.oneTapClient
    }
    private val signInRequest by lazy {
        firebaseViewModel.signInRequest
    }

    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private var idToken: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        initState()
        signInCheck()
        initGoogleClient()
        setListeners()
        setObservers()
    }

    private fun signInCheck() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val googleCredential = oneTapClient.getSignInCredentialFromIntent(it.data)
                idToken = googleCredential.googleIdToken

                when {
                    idToken != null -> {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    if (firebaseUser != null) {
                                        findNavController().navigate(
                                            R.id.main_fragment
                                        )
                                    } else {
                                        findNavController().navigate(R.id.phone_number_fragment)
                                    }
                                    // Sign in success, update UI with the signed-in user's information

                                } else {
                                    // If sign in fails, display a message to the user.
                                    showToast(requireContext(), "${task.exception}")
                                }
                            }
                    }

                    else -> {
                        // Shouldn't happen.
                        showToast(requireContext(), "NO TOKEN!!!")
                    }
                }
            }
        }

//        if (firebaseUser != null)
//            findNavController().navigate(
//                R.id.main_fragment
//            )
    }

    private fun initState() {
        firebaseViewModel.updateSignInState(SignInState.UnsignedIn)
    }

    private fun setObservers() {
        with(firebaseViewModel) {

        }
    }

    private fun initGoogleClient() {

    }


    private fun setListeners() {
        with(binding) {
            googleButton.setOnClickListener {
                firebaseViewModel.updateSignInState(SignInState.Google)
                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(requireActivity()) { result ->
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender)
                                .build()
                        activityResultLauncher.launch(intentSenderRequest)
                    }
                    .addOnFailureListener(requireActivity()) { e ->
                        showToast(requireContext(), e.message!!)
                    }
            }
            phoneNumberButton.setOnClickListener {
                firebaseViewModel.updateSignInState(SignInState.PhoneNumber)
                findNavController().navigate(R.id.phone_number_fragment)
            }
        }
    }
}
