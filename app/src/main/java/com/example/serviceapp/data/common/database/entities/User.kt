package com.example.serviceapp.data.common.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "displayName") var displayName: String,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "phoneNumber") var phoneNumber: String
)