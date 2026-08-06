package com.example.bookify

data class Book(
    var id: String = "",
    val title: String = "",
    val author: String = "",
    val subject: String = "",
    val imageUrl: String = "",
    val rating: Float = 0.0f
)