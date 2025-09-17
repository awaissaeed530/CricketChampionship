package io.awais.cricket_championship.engine.visualization

import io.awais.cricket_championship.engine.result.Inning
import io.awais.cricket_championship.engine.result.MatchResult
import io.awais.cricket_championship.engine.utils.oversFormatted
import io.awais.cricket_championship.engine.utils.runRateFormatted

/**
 * Handles rendering various visualizations for the cricket simulation
 */
class VisualizationRenderer {
    companion object {
        private const val SCOREBOARD_WIDTH = 80

        /**
         * Renders an ASCII art scoreboard for the match
         */
        fun renderScoreboard(match: MatchResult): String {
            val firstInnings = match.firstInnings
            val secondInnings = match.secondInnings

            return """
                ${"-".repeat(SCOREBOARD_WIDTH)}
                |${centerText("CRICKET SCOREBOARD", SCOREBOARD_WIDTH)}|
                |${"-".repeat(SCOREBOARD_WIDTH)}|
                |${formatTeamScore(firstInnings, true).padEnd(SCOREBOARD_WIDTH - 2)}|
                |${formatTeamScore(secondInnings, false, firstInnings.score + 1).padEnd(SCOREBOARD_WIDTH - 2)}|
                |${"-".repeat(SCOREBOARD_WIDTH)}|
                |${centerText("MATCH RESULT", SCOREBOARD_WIDTH)}|
                |${centerText(match.result, SCOREBOARD_WIDTH)}|
                ${"-".repeat(SCOREBOARD_WIDTH)}
            """.trimMargin()
        }

        /**
         * Renders a simple match progression graph
         */
        fun renderMatchProgression(firstInnings: Inning, secondInnings: Inning): String {
            val maxOvers = firstInnings.overs.coerceAtLeast(secondInnings.overs).toInt() + 1
            val firstOvers = firstInnings.overs.toInt()
            val secondOvers = secondInnings.overs.toInt()

            val firstRuns = (0..firstOvers).map { over ->
                firstInnings.ballsBowled.filter { it.bowler == firstInnings.bowlingTeam.players[over % firstInnings.bowlingTeam.players.size] }
                    .sumOf { it.runs }
            }.runningFold(0) { acc, runs -> acc + runs }

            val secondRuns = (0..secondOvers).map { over ->
                secondInnings.ballsBowled.filter { it.bowler == secondInnings.bowlingTeam.players[over % secondInnings.bowlingTeam.players.size] }
                    .sumOf { it.runs }
            }.runningFold(0) { acc, runs -> acc + runs }

            val maxRuns = (firstRuns + secondRuns).maxOrNull()?.coerceAtLeast(1) ?: 1

            return buildString {
                appendLine("\n=== MATCH PROGRESSION ===")
                appendLine("Runs | ${firstInnings.battingTeam.name} (1st) | ${secondInnings.battingTeam.name} (2nd)")
                appendLine("${"-".repeat(8)}|${(1..2).joinToString("|") { "${(1..20).joinToString(" ") { "-" }}" }}")

                val step = (maxRuns.coerceAtLeast(50) / 10).coerceAtLeast(10)

                for (runs in 0..maxRuns step step) {
                    val firstPos = (runs.toDouble() / maxRuns * 20).toInt().coerceAtMost(20)
                    val secondPos = (runs.toDouble() / maxRuns * 20).toInt().coerceAtMost(20)

                    appendLine(
                        "${
                            runs.toString().padStart(4)
                        } | " + "${" ".repeat(firstPos)}•${" ".repeat(20 - firstPos)} | " + "${" ".repeat(secondPos)}•${
                            " ".repeat(
                                20 - secondPos
                            )
                        }"
                    )
                }
            }
        }

        /**
         * Renders a wagon wheel showing shot distribution
         */
        fun renderWagonWheel(innings: Inning): String {
            val shots = innings.ballsBowled.filterNot { it.isWide || it.isNoBall }

            val zones = mutableMapOf<String, Int>()

            // Simple wagon wheel with 6 zones
            listOf(
                "Straight" to 0..30,
                "Cover" to 31..60,
                "Mid-wicket" to 301..330,
                "Square leg" to 271..300,
                "Fine leg" to 241..270,
                "Third man" to 331..360
            ).forEach { (zone, range) ->
                zones[zone] = shots.count { it.runs > 0 && (it.runs % 360) in range }
            }

            val maxShots = zones.values.maxOrNull() ?: 1

            return buildString {
                appendLine("\n=== WAGON WHEEL ===")
                appendLine("Shot distribution for ${innings.battingTeam.name}")

                zones.entries.sortedByDescending { it.value }.forEach { (zone, count) ->
                    val barLength = if (maxShots > 0) (count * 20 / maxShots) else 0
                    appendLine("${zone.padEnd(12)}: ${if (count > 0) "•".repeat(barLength) + " " else ""}$count")
                }
            }
        }

        /**
         * Renders a pitch map showing where balls were bowled
         */
        fun renderPitchMap(innings: Inning): String {
            val deliveries = innings.ballsBowled.filterNot { it.isWide || it.isNoBall }

            // Simple 3x3 grid for pitch map
            val pitchZones = List(3) { MutableList(3) { 0 } }

            deliveries.forEach { ball ->
                // Simple distribution - in a real implementation, we'd use actual ball tracking data
                val row = when {
                    ball.runs == 6 -> 0 // Full tosses
                    ball.isWicket -> 1   // Good length
                    else -> 2            // Yorkers
                }
                val col = (0..2).random() // Random distribution for this example
                pitchZones[row][col]++
            }

            return buildString {
                appendLine("\n=== PITCH MAP ===")
                appendLine("Where the ball was bowled to ${innings.battingTeam.name}")
                appendLine(
                    """
                    +--------+--------+--------+
                    | Full   | Full   | Full   |
                    | ${pitchZones[0][0].toString().padEnd(6)} | ${
                        pitchZones[0][1].toString().padEnd(6)
                    } | ${pitchZones[0][2].toString().padEnd(6)} |
                    +--------+--------+--------+
                    | Good   | Good   | Good   |
                    | ${pitchZones[1][0].toString().padEnd(6)} | ${
                        pitchZones[1][1].toString().padEnd(6)
                    } | ${pitchZones[1][2].toString().padEnd(6)} |
                    +--------+--------+--------+
                    | Yorker | Yorker | Yorker |
                    | ${pitchZones[2][0].toString().padEnd(6)} | ${
                        pitchZones[2][1].toString().padEnd(6)
                    } | ${pitchZones[2][2].toString().padEnd(6)} |
                    +--------+--------+--------+
                """.trimIndent()
                )
            }
        }

        private fun formatTeamScore(innings: Inning, isFirst: Boolean, target: Int? = null): String {
            val score =
                "${innings.battingTeam.name}: ${innings.score}/${innings.wickets} in ${innings.overs.oversFormatted} overs"
            val runRate = "(RR: ${innings.runRate.runRateFormatted})"
            val required = if (!isFirst && target != null) " | Target: $target" else ""

            return "$score $runRate$required"
        }

        private fun centerText(text: String, width: Int): String {
            val padding = (width - text.length) / 2
            return " ".repeat(padding.coerceAtLeast(0)) + text + " ".repeat(
                (width - text.length - padding).coerceAtLeast(
                    0
                )
            )
        }
    }
}
