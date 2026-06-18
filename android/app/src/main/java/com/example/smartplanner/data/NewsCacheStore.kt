package com.example.smartplanner.data

import android.content.Context
import com.example.smartplanner.model.NewsArticle
import org.json.JSONArray
import org.json.JSONObject

class NewsCacheStore(context: Context) {
    private val preferences = context.getSharedPreferences("news_cache", Context.MODE_PRIVATE)
    private val payloadKey = "news_payload"
    private val savedAtKey = "news_saved_at"
    private val maxAgeMillis = 24 * 60 * 60 * 1000L

    fun load(): List<NewsArticle> {
        val savedAt = preferences.getLong(savedAtKey, 0L)
        val payload = preferences.getString(payloadKey, null) ?: return emptyList()

        if (savedAt == 0L || System.currentTimeMillis() - savedAt > maxAgeMillis) {
            clear()
            return emptyList()
        }

        return runCatching {
            val array = JSONArray(payload)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        NewsArticle(
                            title = item.getString("title"),
                            abstractText = item.getString("abstractText"),
                            publishedDate = item.getString("publishedDate"),
                            source = item.getString("source"),
                            section = item.getString("section"),
                            imageUrl = item.optString("imageUrl").ifBlank { null }
                        )
                    )
                }
            }
        }.getOrElse {
            clear()
            emptyList()
        }
    }

    fun save(articles: List<NewsArticle>) {
        val array = JSONArray()
        articles.forEach { article ->
            array.put(
                JSONObject()
                    .put("title", article.title)
                    .put("abstractText", article.abstractText)
                    .put("publishedDate", article.publishedDate)
                    .put("source", article.source)
                    .put("section", article.section)
                    .put("imageUrl", article.imageUrl ?: "")
            )
        }

        preferences.edit()
            .putString(payloadKey, array.toString())
            .putLong(savedAtKey, System.currentTimeMillis())
            .apply()
    }

    private fun clear() {
        preferences.edit().remove(payloadKey).remove(savedAtKey).apply()
    }
}
