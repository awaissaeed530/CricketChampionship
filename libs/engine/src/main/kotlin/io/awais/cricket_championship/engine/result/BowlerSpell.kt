package io.awais.cricket_championship.engine.result

import io.awais.cricket_championship.engine.entity.Player

/**
 * Data class representing a bowler's spell in an innings
 */
data class BowlerSpell(
    val bowler: Player,
    val ballsBowled: Int,
    val maidens: Int,
    val runs: Int,
    val wickets: Int,
    val dots: Int,
    val fours: Int,
    val sixes: Int,
    val wides: Int = 0,
    val noBalls: Int = 0
) {
    /**
     * Returns the number of overs bowled in format X.Y
     */
    val oversFormatted: String
        get() {
            val overs = ballsBowled / 6
            val balls = ballsBowled % 6
            return "$overs.$balls"
        }

    /**
     * Returns the economy rate
     */
    val economyRate: Double
        get() = if (ballsBowled > 0) {
            runs.toDouble() / (ballsBowled / 6.0)
        } else 0.0

    /**
     * Returns the bowling average (runs per wicket)
     */
    val bowlingAverage: Double
        get() = if (wickets > 0) {
            runs.toDouble() / wickets
        } else Double.POSITIVE_INFINITY

    /**
     * Returns the bowling strike rate (balls per wicket)
     */
    val bowlingStrikeRate: Double
        get() = if (wickets > 0) {
            ballsBowled.toDouble() / wickets
        } else Double.POSITIVE_INFINITY
}