package com.example.smartplanner.data

class CachePolicy(
    private val maxAgeMillis: Long,
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) {
    fun isFresh(savedAtMillis: Long): Boolean {
        if (savedAtMillis <= 0L) return false
        return nowProvider() - savedAtMillis <= maxAgeMillis
    }
}
