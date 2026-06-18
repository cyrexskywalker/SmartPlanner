package com.example.smartplanner.data

import com.example.smartplanner.model.NewsArticle
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object NewsService {

    fun fetchTopStories(): List<NewsArticle> {
        val connection = (URL(NewsApiConfig.MOST_POPULAR_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
        }

        try {
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                error("Unexpected response code: $responseCode")
            }

            val payload = connection.inputStream.bufferedReader().use { it.readText() }
            val results = JSONObject(payload).getJSONArray("results")

            return buildList {
                for (index in 0 until results.length()) {
                    val article = results.getJSONObject(index)
                    add(
                        NewsArticle(
                            title = article.optString("title"),
                            abstractText = article.optString("abstract"),
                            publishedDate = formatDate(article.optString("published_date")),
                            source = article.optString("source"),
                            section = article.optString("section"),
                            imageUrl = extractImageUrl(article)
                        )
                    )
                }
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun extractImageUrl(article: JSONObject): String? {
        val mediaList = article.optJSONArray("media") ?: return null
        for (index in 0 until mediaList.length()) {
            val media = mediaList.optJSONObject(index) ?: continue
            val metadata = media.optJSONArray("media-metadata") ?: continue
            for (metaIndex in metadata.length() - 1 downTo 0) {
                val entry = metadata.optJSONObject(metaIndex) ?: continue
                val url = entry.optString("url")
                if (url.isNotBlank()) {
                    return url
                }
            }
        }
        return null
    }

    private fun formatDate(value: String): String {
        return runCatching {
            LocalDate.parse(value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }.getOrDefault(value)
    }
}
