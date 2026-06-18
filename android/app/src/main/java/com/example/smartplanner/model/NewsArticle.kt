package com.example.smartplanner.model

data class NewsArticle(
    val title: String,
    val abstractText: String,
    val publishedDate: String,
    val source: String,
    val section: String,
    val imageUrl: String?
)
