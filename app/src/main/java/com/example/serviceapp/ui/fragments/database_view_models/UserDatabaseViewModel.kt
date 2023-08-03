package com.example.serviceapp.ui.fragments.database_view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.data.domain.databases.user_database.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDatabaseViewModel @Inject constructor(
    private val insertUserUseCase: InsertUserUseCase,
    private val insertAllUserUseCase: InsertAllUsersUseCase,
    getUserDataListUseCase: GetUserDataListUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val deleteAllUserUseCase: DeleteAllUsersUseCase
) : ViewModel() {

    val allUsers: LiveData<List<User>> = getUserDataListUseCase.userDataList

    fun getUser(userId: Int) = viewModelScope.launch {
        getUserUseCase.getUser(userId)
    }

    fun insert(user: User) = viewModelScope.launch {
        insertUserUseCase.insert(user)
    }

    fun insertAll(vararg users: User) = viewModelScope.launch {
        insertAllUserUseCase.insertAll(*users)
    }

    fun delete(user: User) = viewModelScope.launch {
        deleteUserUseCase.delete(user)
    }

    fun deleteAll() = viewModelScope.launch {
        deleteAllUserUseCase.deleteAll()
    }
}