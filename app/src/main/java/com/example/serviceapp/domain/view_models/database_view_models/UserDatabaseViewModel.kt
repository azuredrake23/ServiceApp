package com.example.serviceapp.domain.view_models.database_view_models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.serviceapp.domain.databases.user_database.usecase.DeleteUserUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.GetAllUsersUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.GetUserUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.InsertUserUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.IsUserExistsByEmailUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.IsUserExistsByPhoneUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.coroutineContext


@HiltViewModel
class UserDatabaseViewModel @Inject constructor(
    getAllUsersUseCase: GetAllUsersUseCase,
    private val insertUserUseCase: InsertUserUseCase,
    private val isUserExistsByPhoneUseCase: IsUserExistsByPhoneUseCase,
    private val isUserExistsByEmailUseCase: IsUserExistsByEmailUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {


}