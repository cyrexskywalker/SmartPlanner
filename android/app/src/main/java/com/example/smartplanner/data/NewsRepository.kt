package com.example.smartplanner.data

import android.content.Context
import com.example.smartplanner.model.NewsArticle

class NewsRepository(context: Context) {
    private val cacheStore = NewsCacheStore(context)

    fun cachedArticles(): List<NewsArticle> = cacheStore.load()

    fun refreshArticles(): List<NewsArticle> {
        val articles = NewsService.fetchTopStories()
        cacheStore.save(articles)
        return articles
    }
}
