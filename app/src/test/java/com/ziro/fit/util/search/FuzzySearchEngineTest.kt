package com.ziro.fit.util.search

import org.junit.Assert.*
import org.junit.Test

class FuzzySearchEngineTest {

    // --- calculateLevenshteinDistance tests ---

    @Test
    fun `levenshtein distance of identical strings is zero`() {
        assertEquals(0, FuzzySearchEngine.calculateLevenshteinDistance("hello", "hello"))
    }

    @Test
    fun `levenshtein distance of empty and non-empty is length of non-empty`() {
        assertEquals(5, FuzzySearchEngine.calculateLevenshteinDistance("", "hello"))
        assertEquals(5, FuzzySearchEngine.calculateLevenshteinDistance("hello", ""))
    }

    @Test
    fun `levenshtein distance of two empty strings is zero`() {
        assertEquals(0, FuzzySearchEngine.calculateLevenshteinDistance("", ""))
    }

    @Test
    fun `levenshtein distance is case insensitive`() {
        assertEquals(0, FuzzySearchEngine.calculateLevenshteinDistance("Hello", "hello"))
        assertEquals(0, FuzzySearchEngine.calculateLevenshteinDistance("HELLO", "hello"))
    }

    @Test
    fun `levenshtein distance with whitespace is trimmed`() {
        assertEquals(0, FuzzySearchEngine.calculateLevenshteinDistance("  hello", "hello"))
        assertEquals(0, FuzzySearchEngine.calculateLevenshteinDistance("hello  ", "hello"))
    }

    @Test
    fun `levenshtein distance of one character substitution`() {
        assertEquals(1, FuzzySearchEngine.calculateLevenshteinDistance("cat", "bat"))
    }

    @Test
    fun `levenshtein distance of one character insertion`() {
        assertEquals(1, FuzzySearchEngine.calculateLevenshteinDistance("cat", "cats"))
    }

    @Test
    fun `levenshtein distance of one character deletion`() {
        assertEquals(1, FuzzySearchEngine.calculateLevenshteinDistance("cats", "cat"))
    }

    @Test
    fun `levenshtein distance of completely different strings`() {
        assertEquals(6, FuzzySearchEngine.calculateLevenshteinDistance("abcdef", "ghijkl"))
    }

    @Test
    fun `levenshtein distance of similar words`() {
        val distance = FuzzySearchEngine.calculateLevenshteinDistance("bench press", "bench")
        assertTrue(distance > 0)
        assertTrue(distance < 12)
    }

    // --- calculateSimilarity tests ---

    @Test
    fun `similarity of identical strings is one`() {
        assertEquals(1.0, FuzzySearchEngine.calculateSimilarity("hello", "hello"), 0.0)
    }

    @Test
    fun `similarity of completely different strings approaches zero`() {
        val sim = FuzzySearchEngine.calculateSimilarity("abc", "xyz")
        assertTrue(sim < 0.5)
    }

    @Test
    fun `similarity of empty strings is one`() {
        assertEquals(1.0, FuzzySearchEngine.calculateSimilarity("", ""), 0.0)
    }

    @Test
    fun `similarity of one empty string is zero`() {
        assertEquals(0.0, FuzzySearchEngine.calculateSimilarity("hello", ""), 0.0)
        assertEquals(0.0, FuzzySearchEngine.calculateSimilarity("", "hello"), 0.0)
    }

    @Test
    fun `similarity is case insensitive`() {
        val sim1 = FuzzySearchEngine.calculateSimilarity("HELLO", "hello")
        assertEquals(1.0, sim1, 0.0)
    }

    @Test
    fun `similarity is higher for closer strings`() {
        val simBenchBenchPress = FuzzySearchEngine.calculateSimilarity("bench", "bench press")
        val simBenchSquat = FuzzySearchEngine.calculateSimilarity("bench", "squat")
        assertTrue(simBenchBenchPress > simBenchSquat)
    }

    // --- findBestMatch tests ---

    @Test
    fun `findBestMatch returns exact match`() {
        val candidates = listOf("bench press", "squat", "deadlift")
        val result = FuzzySearchEngine.findBestMatch("bench press", candidates)
        assertEquals("bench press", result)
    }

    @Test
    fun `findBestMatch returns partial match above threshold`() {
        val candidates = listOf("bench press", "squat", "deadlift")
        val result = FuzzySearchEngine.findBestMatch("bench", candidates)
        assertEquals("bench press", result)
    }

    @Test
    fun `findBestMatch returns null for blank query`() {
        val candidates = listOf("bench press")
        assertNull(FuzzySearchEngine.findBestMatch("", candidates))
        assertNull(FuzzySearchEngine.findBestMatch("   ", candidates))
    }

    @Test
    fun `findBestMatch returns null when nothing meets threshold`() {
        val candidates = listOf("bench press", "squat")
        val result = FuzzySearchEngine.findBestMatch("xyz", candidates, threshold = 0.5)
        assertNull(result)
    }

    @Test
    fun `findBestMatch uses custom threshold`() {
        val candidates = listOf("bench press", "squat")
        val result = FuzzySearchEngine.findBestMatch("squat", candidates, threshold = 0.8)
        assertEquals("squat", result)
        val noMatch = FuzzySearchEngine.findBestMatch("xyz", candidates, threshold = 0.8)
        assertNull(noMatch)
    }

    @Test
    fun `findBestMatch returns first best match when multiple meet threshold`() {
        val candidates = listOf("bench press", "bench mark")
        val result = FuzzySearchEngine.findBestMatch("bench", candidates, threshold = 0.3)
        assertNotNull(result)
    }

    // --- findBestMatches tests ---

    @Test
    fun `findBestMatches returns ordered by similarity descending`() {
        val candidates = listOf("bench press", "squat", "deadlift", "bench mark")
        val results = FuzzySearchEngine.findBestMatches("bench", candidates)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].second >= results[1].second)
    }

    @Test
    fun `findBestMatches respects limit`() {
        val candidates = listOf("a", "b", "c", "d", "e", "f")
        val results = FuzzySearchEngine.findBestMatches("x", candidates, threshold = 0.0, limit = 3)
        assertEquals(3, results.size)
    }

    @Test
    fun `findBestMatches returns empty for blank query`() {
        val results = FuzzySearchEngine.findBestMatches("", listOf("a", "b"))
        assertTrue(results.isEmpty())
    }

    @Test
    fun `findBestMatches filters below threshold`() {
        val results = FuzzySearchEngine.findBestMatches("xyz", listOf("abc", "def"), threshold = 0.3)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `findBestMatches returns all matches above threshold up to limit`() {
        val results = FuzzySearchEngine.findBestMatches("bench", listOf("bench", "bench press", "deficit"), threshold = 0.3, limit = 5)
        assertTrue(results.size <= 5)
        assertTrue(results.all { it.second >= 0.3 })
    }

    // --- tokenAwareSimilarity tests ---

    @Test
    fun `tokenAwareSimilarity returns 1 for exact match`() {
        val sim = FuzzySearchEngine.tokenAwareSimilarity("bench press", "bench press")
        assertEquals(1.0, sim, 0.0)
    }

    @Test
    fun `tokenAwareSimilarity gives bonus for substring match`() {
        val withBonus = FuzzySearchEngine.tokenAwareSimilarity("bench", "bench press")
        val withoutBonus = FuzzySearchEngine.calculateSimilarity("bench", "bench press")
        assertTrue(withBonus > withoutBonus)
    }

    @Test
    fun `tokenAwareSimilarity handles multiple tokens`() {
        val sim = FuzzySearchEngine.tokenAwareSimilarity("bench press", "press")
        assertTrue(sim > 0.0)
    }

    @Test
    fun `tokenAwareSimilarity falls back to regular similarity for blank tokens`() {
        val sim = FuzzySearchEngine.tokenAwareSimilarity("   ", "bench")
        assertEquals(FuzzySearchEngine.calculateSimilarity("   ", "bench"), sim, 0.0)
    }

    @Test
    fun `tokenAwareSimilarity caps at 1`() {
        val sim = FuzzySearchEngine.tokenAwareSimilarity("bench", "bench press extra words")
        assertTrue(sim <= 1.0)
    }

    // --- String similarity extension ---

    @Test
    fun `similarity extension calls engine correctly`() {
        val sim = "bench".similarity("bench press")
        assertEquals(FuzzySearchEngine.calculateSimilarity("bench", "bench press"), sim, 0.0)
    }
}
