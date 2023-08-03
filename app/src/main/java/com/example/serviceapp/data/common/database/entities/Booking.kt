package com.example.serviceapp.data.common.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "booking", foreignKeys = [ForeignKey(entity = User::class,
    parentColumns = ["id"],
    childColumns = ["id"],
    onDelete = ForeignKey.CASCADE)]
)
data class Booking(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "time") var time: String,
    @ColumnInfo(name = "id_user") var id_user: Int,
    @ColumnInfo(name = "id_service_master") var id_service_master: Int,
    @ColumnInfo(name = "master_name") var master_name: String
)