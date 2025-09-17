package io.awais.cricket_championship.engine.result

/**
 * Data class representing a batting partnership between two or more batsmen
 */
data class Partnership(
    val batsmen: List<BatsmanInnings>,
    val runs: Int,
    val balls: Int,
    val startOver: Float,
    val endOver: Float
) {
    val runRate: Double
        get() = if (balls > 0) runs.toDouble() / (balls / 6.0) else 0.0

    val overs: String
        get() {
            val completedOvers = balls / 6
            val ballsInCurrentOver = balls % 6
            return if (ballsInCurrentOver > 0) "$completedOvers.${ballsInCurrentOver}" else "$completedOvers"
        }

    /**
     * Returns a formatted string representation of the partnership
     */
    fun format(): String {
        val batsmenNames = batsmen.joinToString(" & ") { it.player.name }
        val runRateStr = String.format("%.2f", runRate)
        return "$runs (${overs} ov, RR: $runRateStr) - $batsmenNames"
    }

    /**
     * Returns a detailed breakdown of the partnership
     */
    fun detailedBreakdown(): String {
        val builder = StringBuilder()
        builder.appendLine("Partnership: $runs runs in ${overs} overs (RR: ${String.format("%.2f", runRate)})")
        builder.appendLine("Batsmen:")
        
        batsmen.sortedByDescending { it.runs }.forEach { batsman ->
            val ballsFaced = batsman.ballsFaced.coerceAtLeast(1)
            val strikeRate = (batsman.runs * 100.0 / ballsFaced).toInt()
            builder.appendLine("  • ${batsman.player.name}: ${batsman.runs} (${ballsFaced}b, ${batsman.fours}x4, ${batsman.sixes}x6, SR: $strikeRate)")
        }
        
        return builder.toString()
    }
}
