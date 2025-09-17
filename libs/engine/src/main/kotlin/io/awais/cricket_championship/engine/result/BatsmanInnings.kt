package io.awais.cricket_championship.engine.result

import io.awais.cricket_championship.engine.entity.Player

/**
 * Data class representing a batsman's innings
 */
data class BatsmanInnings(
    val player: Player,
    val position: Int,
    val runs: Int = 0,
    val ballsFaced: Int = 0,
    val isOut: Boolean = false,
    val dismissalType: String? = null,
    val fielder: String? = null,
    val fours: Int = 0,
    val sixes: Int = 0,
    val entryOver: Float = 0f
) {
    /**
     * Calculates the batsman's strike rate
     */
    val strikeRate: Double
        get() = if (ballsFaced > 0) runs * 100.0 / ballsFaced else 0.0
        
    /**
     * Calculates the batsman's average (returns null if not out)
     */
    val average: Double?
        get() = if (isOut) runs.toDouble() else null
        
    /**
     * Returns a formatted string of the batsman's score
     */
    fun scoreString(): String {
        val notOut = if (!isOut) "*" else ""
        return "$runs$notOut (${ballsFaced}b ${fours}x4 ${sixes}x6)"
    }
}