package com.example.serviceapp.data.common.database.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "photo") var photo: String? = null,
    @ColumnInfo(name = "displayName") var displayName: String? = null,
    @ColumnInfo(name = "email") var email: String? = null,
    @ColumnInfo(name = "phoneNumber") var phoneNumber: String? = null
)