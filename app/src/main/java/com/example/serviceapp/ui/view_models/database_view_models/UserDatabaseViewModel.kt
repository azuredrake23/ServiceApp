package com.example.serviceapp.ui.view_models.database_view_models

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.domain.databases.user_database.usecase.GetUserDataListUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.GetUserUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.InsertUserUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDatabaseViewModel @Inject constructor(
    private val insertUserUseCase: InsertUserUseCase,
//    private val insertAllUserUseCase: InsertAllUsersUseCase,
    getUserDataListUseCase: GetUserDataListUseCase,
    private val getUserUseCase: GetUserUseCase,
//    private val deleteUserUseCase: DeleteUserUseCase,
//    private val deleteAllUserUseCase: DeleteAllUsersUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
//    private val updateAllUsersUseCase: UpdateAllUsersUseCase,
//    getUserRegisteredUseCase: GetUserDataWithSignInStateUseCase
) : ViewModel() {
    //
    private val _currentUser = MutableSharedFlow<Boolean>()
    val currentUser: Flow<Boolean> get() = _currentUser

//    fun getUser(userId: Int) = viewModelScope.launch {
//        getUserUseCase.getUser(userId)
//    }

    fun insert(user: User) = viewModelScope.launch {
        insertUserUseCase.insert(user)
    }

    fun update(user: User) = viewModelScope.launch {
        updateUserUseCase.update(user)
    }

    fun isUserExistsByPhoneNumber(phoneNumber: String) = viewModelScope.launch {
        val isUserExists = getUserUseCase.isUserExistsByPhoneNumber(phoneNumber)
        _currentUser.emit(isUserExists)
    }

//    fun insertAll(vararg users: User) = viewModelScope.launch {
//        insertAllUserUseCase.insertAll(*users)
//    }

//    fun delete(user: User) = viewModelScope.launch {
//        deleteUserUseCase.delete(user)
//    }

//    fun deleteAll() = viewModelScope.launch {
//        deleteAllUserUseCase.deleteAll()
//    }
}