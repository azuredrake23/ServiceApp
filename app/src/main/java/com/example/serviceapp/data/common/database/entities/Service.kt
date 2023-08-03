package com.example.serviceapp.data.common.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service")
data class Service(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "service_type") var service_type: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "price") var price: Double,
    @ColumnInfo(name = "time") var time: String
)