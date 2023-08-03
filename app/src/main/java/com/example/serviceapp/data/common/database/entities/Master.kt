package com.example.serviceapp.data.common.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "master")
data class Master(
    @PrimaryKey (autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "experience") var experience: Double,
    @ColumnInfo(name = "rating") var rating: Double
)