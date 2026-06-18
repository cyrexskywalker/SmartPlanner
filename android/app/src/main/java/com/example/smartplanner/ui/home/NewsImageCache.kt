package com.example.smartplanner.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest
import com.example.smartplanner.data.CachePolicy

object NewsImageCache {
    private val memoryCache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8).toInt()) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }
    private val cachePolicy = CachePolicy(maxAgeMillis = 24 * 60 * 60 * 1000L)

    fun load(url: String, cacheDir: File): Bitmap? {
        memoryCache.get(url)?.let { return it }

        val file = imageFile(cacheDir, url)
        if (file.exists()) {
            if (!cachePolicy.isFresh(file.lastModified())) {
                file.delete()
            } else {
                BitmapFactory.decodeFile(file.absolutePath)?.let { bitmap ->
                    memoryCache.put(url, bitmap)
                    return bitmap
                }
            }
        }

        val downloaded = runCatching {
            URL(url).openStream().use { BitmapFactory.decodeStream(it) }
        }.getOrNull() ?: return null

        memoryCache.put(url, downloaded)
        saveToDisk(file, downloaded)
        return downloaded
    }

    private fun saveToDisk(file: File, bitmap: Bitmap) {
        file.parentFile?.mkdirs()
        runCatching {
            FileOutputStream(file).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
            }
        }
    }

    private fun imageFile(cacheDir: File, url: String): File {
        val directory = File(cacheDir, "news_images")
        return File(directory, sha1(url) + ".jpg")
    }

    private fun sha1(input: String): String {
        val digest = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
