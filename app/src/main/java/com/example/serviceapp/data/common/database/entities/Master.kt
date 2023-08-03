package com.example.serviceapp.data.common.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "master")
data class Master(
    @PrimaryKey (autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "specialization") var specialization: String,
    @ColumnInfo(name = "experience_age") var experience_age: Double,
    @ColumnInfo(name = "rating") var rating: Double
)