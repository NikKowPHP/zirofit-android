package com.ziro.fit.util.search

object FuzzySearchEngine {

    fun calculateLevenshteinDistance(s1: String, s2: String): Int {
        val str1 = s1.lowercase().trim()
        val str2 = s2.lowercase().trim()

        if (str1 == str2) return 0
        if (str1.isEmpty()) return str2.length
        if (str2.isEmpty()) return str1.length

        val dp = Array(str1.length + 1) { IntArray(str2.length + 1) }

        for (i in 0..str1.length) {
            dp[i][0] = i
        }
        for (j in 0..str2.length) {
            dp[0][j] = j
        }

        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }

        return dp[str1.length][str2.length]
    }

    fun calculateSimilarity(s1: String, s2: String): Double {
        val distance = calculateLevenshteinDistance(s1, s2)
        val maxLength = maxOf(s1.length, s2.length)
        if (maxLength == 0) return 1.0
        return 1.0 - (distance.toDouble() / maxLength.toDouble())
    }

    fun findBestMatch(query: String, candidates: List<String>, threshold: Double = 0.3): String? {
        if (query.isBlank()) return null
        
        var bestMatch: String? = null
        var bestScore = threshold

        for (candidate in candidates) {
            val score = calculateSimilarity(query, candidate)
            if (score > bestScore) {
                bestScore = score
                bestMatch = candidate
            }
        }

        return bestMatch
    }

    fun findBestMatches(query: String, candidates: List<String>, threshold: Double = 0.3, limit: Int = 5): List<Pair<String, Double>> {
        if (query.isBlank()) return emptyList()

        return candidates
            .map { candidate -> Pair(candidate, calculateSimilarity(query, candidate)) }
            .filter { it.second >= threshold }
            .sortedByDescending { it.second }
            .take(limit)
    }

    fun tokenAwareSimilarity(query: String, target: String): Double {
        val queryTokens = query.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        val targetLower = target.lowercase()

        if (queryTokens.isEmpty()) return calculateSimilarity(query, target)

        val tokenScores = queryTokens.map { token ->
            if (targetLower.contains(token)) {
                1.0
            } else {
                calculateSimilarity(token, targetLower)
            }
        }

        val exactMatchBonus = if (targetLower.contains(query.lowercase())) 0.2 else 0.0
        
        return (tokenScores.average() + exactMatchBonus).coerceAtMost(1.0)
    }
}

fun String.similarity(other: String): Double {
    return FuzzySearchEngine.calculateSimilarity(this, other)
}
