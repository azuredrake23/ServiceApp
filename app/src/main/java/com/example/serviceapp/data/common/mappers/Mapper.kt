package com.example.serviceapp.data.common.mappers

interface Mapper<T, R> {
    fun map(data: T): R
}