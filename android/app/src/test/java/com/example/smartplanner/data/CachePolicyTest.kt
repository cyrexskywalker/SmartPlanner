package com.example.smartplanner.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CachePolicyTest {
    @Test
    fun isFresh_returnsTrueWhenSavedAtIsInsideMaxAge() {
        val policy = CachePolicy(maxAgeMillis = 1_000, nowProvider = { 5_000 })

        assertTrue(policy.isFresh(savedAtMillis = 4_500))
    }

    @Test
    fun isFresh_returnsFalseWhenSavedAtIsExpiredOrMissing() {
        val policy = CachePolicy(maxAgeMillis = 1_000, nowProvider = { 5_000 })

        assertFalse(policy.isFresh(savedAtMillis = 3_999))
        assertFalse(policy.isFresh(savedAtMillis = 0))
    }
}
