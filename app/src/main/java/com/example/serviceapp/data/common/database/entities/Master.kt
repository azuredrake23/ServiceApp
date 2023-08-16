package com.example.serviceapp.data.common.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "master", foreignKeys = [ForeignKey(entity = Service::class, parentColumns = ["id"], childColumns = ["id"], onDelete = ForeignKey.CASCADE)])
data class Master(
    @PrimaryKey (autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "master") var master: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "experience") var experience: Double,
    @ColumnInfo(name = "rating") var rating: Double
)