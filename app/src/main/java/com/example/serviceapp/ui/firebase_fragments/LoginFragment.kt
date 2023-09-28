package com.example.serviceapp.ui.firebase_fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.serviceapp.R
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.common.utils.showToast
import com.example.serviceapp.databinding.LoginFragmentBinding
import com.example.serviceapp.ui.view_models.database_view_models.UserDatabaseViewModel
import com.example.serviceapp.ui.view_models.firebase_view_models.FirebaseViewModel
import com.example.serviceapp.utils.DownloadDialog
import com.example.serviceapp.utils.SignInState
import com.example.serviceapp.utils.SignUpState
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {

    private val binding by viewBinding(LoginFragmentBinding::bind)
    private val userViewModel: UserDatabaseViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()

    private val firebaseAuth by lazy {
        firebaseViewModel.firebaseAuth
    }
    private val firebaseRealtimeDatabaseUserReference by lazy {
        firebaseViewModel.firebaseRealtimeDatabaseUserRef
    }
    private val oneTapClient by lazy {
        firebaseViewModel.oneTapClient
    }
    private val signInRequest by lazy {
        firebaseViewModel.signInRequest
    }
    private val downloadDialog by lazy {
        DownloadDialog(requireContext())
    }

    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private var idToken: String? = null

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
                val googleCredential = oneTapClient.getSignInCredentialFromIntent(it.data)
                idToken = googleCredential.googleIdToken
                if (idToken != null) {
                    downloadDialog.showDownloadDialog()
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            googleSignInExecution(task, firebaseCredential, googleCredential)
                        }
                } else {
                    // Shouldn't happen.
                    showToast(requireContext(), "NO TOKEN!!!")
                }
            }
        }
    }

    private fun googleSignInExecution(
        task: Task<AuthResult>,
        firebaseCredential: AuthCredential,
        googleCredential: SignInCredential
    ) {
        if (task.isSuccessful) {
            firebaseAuth.currentUser!!.reauthenticate(firebaseCredential).addOnCompleteListener {
                firebaseAuth.currentUser!!.updateEmail(googleCredential.id)
                firebaseAuth.currentUser!!.updateProfile(userProfileChangeRequest {
                    displayName = googleCredential.displayName
                })
            }

            navigation(task, firebaseCredential, googleCredential)
        } else {
            showToast(requireContext(), "${task.exception}")
        }
    }

    private fun navigation(
        task: Task<AuthResult>,
        firebaseCredential: AuthCredential,
        googleCredential: SignInCredential
    ) {
        firebaseRealtimeDatabaseUserReference.child(firebaseAuth.currentUser!!.uid)
            .child("phoneNumber").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    downloadDialog.cancelDownloadDialog()
                    if (dataSnapshot.value != null) {
                        /** USER HAS PHONE NUMBER **/
                        firebaseViewModel.updateSignUpState(SignUpState.SignedUp)
                        findNavController().navigate(
                            R.id.main_fragment
                        )
                        showToast(
                            requireContext(),
                            getString(R.string.successful_authentication_message)
                        )
                    } else {
                        /** USER DOESN'T HAS PHONE NUMBER **/
                        firebaseViewModel.updateSignUpState(SignUpState.UnsignedUp)
                        firebaseAuth.currentUser!!.linkWithCredential(
                            firebaseCredential
                        ).addOnCompleteListener {
                            if (task.isSuccessful) {
                                firebaseAuth.currentUser!!.updateProfile(
                                    userProfileChangeRequest {
                                        displayName = googleCredential.displayName
                                        if (googleCredential.profilePictureUri != null){
                                            photoUri = googleCredential.profilePictureUri
                                        }
                                    })
                                firebaseAuth.currentUser!!.updateEmail(
                                    googleCredential.id
                                )
                                findNavController().navigate(
                                    R.id.phone_number_fragment
                                )
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    private fun setObservers() {
        with(userViewModel) {
        }
    }

    private fun setListeners() {
        with(binding) {
            googleButton.setOnClickListener {
                downloadDialog.showDownloadDialog()
                firebaseViewModel.updateSignInState(SignInState.Google)

                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(requireActivity()) { result ->
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        activityResultLauncher.launch(intentSenderRequest)
                        downloadDialog.cancelDownloadDialog()
                    }.addOnFailureListener(requireActivity()) { e ->
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
