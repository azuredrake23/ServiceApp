package com.example.serviceapp.data.models

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(val photo: String? = null, val displayName: String? = null, val email: String? = null, val phoneNumber: String? = null)