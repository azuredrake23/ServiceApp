package com.example.serviceapp.ui.view_models.database_view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.domain.databases.user_database.usecase.DeleteUserUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.GetAllUsersUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.GetUserUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.InsertUserUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.IsUserExistsByEmailUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.IsUserExistsByPhoneUseCase
import com.example.serviceapp.domain.databases.user_database.usecase.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    //
    private val _isUserExists = MutableStateFlow(false)
    val isUserExists: Flow<Boolean> get() = _isUserExists

    private val _user = MutableSharedFlow<User?>()
    val user: SharedFlow<User?> get() = _user

    private val users = getAllUsersUseCase.getAllUsersUseCase

    fun isUserExistsByEmail(email: String): Boolean {
        viewModelScope.launch {
            val isUserExists = isUserExistsByEmailUseCase.check(email)
            _isUserExists.emit(isUserExists)
        }
        return _isUserExists.value
    }

    fun isUserExistsByPhoneNumber(phoneNumber: String): Boolean {
        viewModelScope.launch {
            val isUserExists = isUserExistsByPhoneUseCase.check(phoneNumber)
            _isUserExists.emit(isUserExists)
        }
        return _isUserExists.value
    }

    fun getUser(email: String? = null, phoneNumber: String ? = null) = viewModelScope.launch {
        val user = getUserUseCase.getUser(email, phoneNumber)
        _user.emit(user)
    }

    fun insert(user: User) = viewModelScope.launch {
        insertUserUseCase.insert(user)
    }

    fun update(email: String, phoneNumber: String, user: User) = viewModelScope.launch {
        updateUserUseCase.update(email, phoneNumber, user)
    }

    fun delete(email: String? = null, phoneNumber: String? = null) = viewModelScope.launch {
        deleteUserUseCase.delete(email, phoneNumber)
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