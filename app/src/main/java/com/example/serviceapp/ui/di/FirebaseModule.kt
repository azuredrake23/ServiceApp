package com.example.serviceapp.ui.di

import android.content.Context
import com.example.serviceapp.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module(includes = [CommonModule::class])
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseRealtimeDatabaseUserRef(firebaseAuth: FirebaseAuth): DatabaseReference =
        if (firebaseAuth.currentUser != null) {
            FirebaseDatabase.getInstance("https://service-app-e0bb2-default-rtdb.firebaseio.com").reference.child(
                "users"
            ).child(firebaseAuth.currentUser!!.uid)
        } else FirebaseDatabase.getInstance("https://service-app-e0bb2-default-rtdb.firebaseio.com").reference.child(
            "users"
        )

    @Singleton
    @Provides
    fun provideFirebaseOneTapClient(@ApplicationContext context: Context): SignInClient =
        Identity.getSignInClient(context)

    @Singleton
    @Provides
    fun provideFirebaseBeginSignInClient(@ApplicationContext context: Context): BeginSignInRequest =
        BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(context.getString(R.string.your_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

}