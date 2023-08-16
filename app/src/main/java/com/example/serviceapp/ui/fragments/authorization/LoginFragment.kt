package com.example.serviceapp.ui.fragments.authorization

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.LoginFragmentBinding
import com.example.serviceapp.ui.fragments.database_view_models.UserDatabaseViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient

class LoginFragment : Fragment(R.layout.login_fragment) {

    private val binding by viewBinding(LoginFragmentBinding::bind)
    private val userViewModel: UserDatabaseViewModel by activityViewModels()

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setupUI()
    }
    private fun setupUI() {
        initGoogleClient()
        setListeners()
    }

    private fun initGoogleClient() {

        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
//                    .setPasswordRequestOptions(
//                        BeginSignInRequest.PasswordRequestOptions.builder()
//                        .setSupported(true)
//                        .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
//                    .setAutoSelectEnabled(true)
            .build()

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
                val idToken = credential.googleIdToken
                when {
                    idToken != null -> {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        val email = credential.id
                        showToast(requireContext(), "GOT TOKEN!!!")
                    }

                    else -> {
                        // Shouldn't happen.
                        showToast(requireContext(), "NO TOKEN!!!")
                    }
                }
            }
        }
    }

    private fun setObservers() {
        with(userViewModel) {
//            userData
//                .flowWithLifecycle(
//                    viewLifecycleOwner.lifecycle,
//                    Lifecycle.State.STARTED
//                )
//                .onEach { answer ->
//                    checkUserRegistration(answer)
//                }
//                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun setListeners() {
        signInClickListeners()
    }

    private fun signInClickListeners() {
        with(binding) {
            googleButton.setOnClickListener {

                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(requireActivity()) { result ->
                            val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                            activityResultLauncher.launch(intentSenderRequest)
                    }
                    .addOnFailureListener(requireActivity()) {
                            e ->
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        showToast(requireContext(), e.message!!)
                    }
            }
            phoneNumberButton.setOnClickListener{
                findNavController().navigate(R.id.phone_number_fragment)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
