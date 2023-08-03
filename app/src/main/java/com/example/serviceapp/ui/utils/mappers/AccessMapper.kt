package com.example.serviceapp.ui.utils.mappers

import com.example.serviceapp.data.common.mappers.Mapper

object AccessMapper: Mapper<String, Boolean> {

    override fun map(data: String): Boolean =
        when (data) {
            Access.USER.name -> false
            Access.ADMIN.name -> true
            else -> false
        }

}

enum class Access {
    USER,
    ADMIN
}
