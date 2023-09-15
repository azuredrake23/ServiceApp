package com.example.serviceapp.data.common.di

import android.content.Context
import com.example.serviceapp.R
import com.example.serviceapp.data.firebase.FirebaseRepositoryImpl
import com.example.serviceapp.domain.firebase.FirebaseRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.annotation.Nullable
import javax.inject.Singleton

@Module(includes = [CommonModule::class])
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Nullable
    @Provides
    fun provideFirebaseCurrentUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser

    @Singleton
    @Provides
    fun provideFirebaseRealtimeDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance("https://service-app-e0bb2-default-rtdb.firebaseio.com")

    @Singleton
    @Provides
    fun provideFirebaseRealtimeDatabaseUserReference(): DatabaseReference = FirebaseDatabase.getInstance("https://service-app-e0bb2-default-rtdb.firebaseio.com").reference.child("users")

    @Singleton
    @Provides
    fun provideFirebaseRepository(): FirebaseRepository = FirebaseRepositoryImpl()

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