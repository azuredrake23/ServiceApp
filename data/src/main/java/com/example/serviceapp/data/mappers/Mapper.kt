package com.example.serviceapp.data.mappers

interface Mapper<T, R> {
    fun map(data: T): R
}