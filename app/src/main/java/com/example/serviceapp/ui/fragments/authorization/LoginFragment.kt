package com.example.serviceapp.ui.fragments.authorization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment(R.layout.login_fragment) {

    private val binding by viewBinding(LoginFragmentBinding::bind)
    private val userViewModel: UserDatabaseViewModel by activityViewModels()

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        onUserSignedIn()
        initGoogleClient()
        setListeners()
    }

    private fun onUserSignedIn() {
        firebaseAuth.signOut()
        if (firebaseAuth.currentUser != null)
            transferToMainFragmentWithData()
    }

    private fun transferToMainFragmentWithData(){
        findNavController().navigate(
            R.id.main_fragment,
            bundleOf("firebaseAuthUser" to firebaseAuth.currentUser)
        )
    }

    private fun initGoogleClient() {
        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
                    .setPasswordRequestOptions(
                        BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val googleCredential = oneTapClient.getSignInCredentialFromIntent(it.data)
                val idToken = googleCredential.googleIdToken
                when {
                    idToken != null -> {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    val firebaseUser = firebaseAuth.currentUser
                                    findNavController().navigate(
                                        R.id.main_fragment,
                                        bundleOf("firebaseUser" to firebaseUser)
                                    )
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
    }


    private fun setListeners() {
        with(binding) {
            googleButton.setOnClickListener {

                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(requireActivity()) { result ->
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        activityResultLauncher.launch(intentSenderRequest)
                    }
                    .addOnFailureListener(requireActivity()) { e ->
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        showToast(requireContext(), e.message!!)
                    }
            }
            phoneNumberButton.setOnClickListener {
                findNavController().navigate(R.id.phone_number_fragment)
            }
//            continueLoginButton.setOnClickListener {
//                if (emailED.text.toString().isNotEmpty()) {
//                    val actionCodeSettings = actionCodeSettings {
//                        // URL you want to redirect back to. The domain (www.example.com) for this
//                        // URL must be whitelisted in the Firebase Console.
//                        url = "https://www.example.com/finishSignUp?cartId=1234"
//                        // This must be true
//                        handleCodeInApp = true
////            setIOSBundleId("com.example.ios")
//                        setAndroidPackageName(
//                            "com.example.serviceapp",
//                            true, // installIfNotAvailable
//                            "23", // minimumVersion
//                        )
//                    }
//
//                    firebaseAuth.sendSignInLinkToEmail(emailED.text.toString(), actionCodeSettings)
////                        .addOnCompleteListener { task ->
////                            if (task.isSuccessful) {
////                                findNavController().navigate(R.id.main_fragment)
////                            } else {
////                                showToast(
////                                    requireContext(), getString(R.string.)
////                                )
////                            }
////                        }
//                    if (firebaseAuth.isSignInWithEmailLink(emailLink)) {
//                        // Retrieve this from wherever you stored it
//
//                        // The client SDK will parse the code from the link for you.
//                        firebaseAuth.signInWithEmailLink(emailED.text.toString(), emailLink)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
////                                    Log.d(TAG, "Successfully signed in with email link!")
//                                    val result = task.result
//                                    // You can access the new user via result.getUser()
//                                    // Additional user info profile *not* available via:
//                                    // result.getAdditionalUserInfo().getProfile() == null
//                                    // You can check if the user is new or existing:
//                                    // result.getAdditionalUserInfo().isNewUser()
//                                } else {
////                                    Log.e(TAG, "Error signing in with email link", task.exception)
//                                }
//                            }
//                    }
//                }
//            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
