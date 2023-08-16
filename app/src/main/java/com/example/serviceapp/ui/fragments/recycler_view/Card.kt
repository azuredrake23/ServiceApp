package com.example.serviceapp.ui.fragments.recycler_view

import com.example.serviceapp.data.common.database.entities.Master

data class Card (val typeOfService: String, val descriptionOfService: String, val priceOfService: Double, val time: String, val master: String, val descriptionOfMaster: String, val experience: Double, val rating: Double)