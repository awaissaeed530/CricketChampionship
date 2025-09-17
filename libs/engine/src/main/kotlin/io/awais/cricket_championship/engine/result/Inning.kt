package io.awais.cricket_championship.engine.result

import io.awais.cricket_championship.engine.BallOutcome
import io.awais.cricket_championship.engine.OverOutcome
import io.awais.cricket_championship.engine.entity.Team
import io.awais.cricket_championship.engine.utils.oversFormatted
import io.awais.cricket_championship.engine.utils.runRateFormatted
import kotlin.collections.iterator
import kotlin.collections.plus

data class Inning(
    val battingTeam: Team,
    val bowlingTeam: Team,
    var score: Int = 0,
    var wickets: Int = 0,
    var overs: Double = 0.0,
    var extras: Int = 0,
    val totalOvers: Int,
    var batsmenStats: MutableList<BatsmanInnings> = mutableListOf(),
    var bowlerSpells: MutableList<BowlerSpell> = mutableListOf(),
    val oversBowled: MutableList<OverOutcome> = mutableListOf(),
    val target: Int? = null
) {
    val ballsBowled: List<BallOutcome>
        get() = oversBowled.flatMap { it.balls }

    val remainingBalls: Int
        get() = ballsBowled.count { !it.isWide && !it.isNoBall }

    val noBalls: Int
        get() = oversBowled.sumOf { over -> over.balls.count { it.isNoBall } }

    val wides: Int
        get() = oversBowled.sumOf { over -> over.balls.count { it.isWide } }

    /**
     * Return the current run rate for the batting team
     */
    val runRate: Double
        get() = if (ballsBowled.isNotEmpty()) {
            score.toDouble() / (ballsBowled.size / 6.0)
        } else 0.0

    /**
     * Returns the required run rate for the chasing team (if applicable)
     */
    val requiredRunRate: Double?
        get() = if (target == null) null else {
            val runsNeeded = target - score - 1
            val ballsRemaining = (totalOvers * 6) - ballsBowled.size

            if (ballsRemaining > 0 && runsNeeded > 0) {
                runsNeeded.toDouble() / (ballsRemaining / 6.0)
            } else null
        }

    /**
     * Returns the list of partnerships in this innings
     */
    val partnerships: List<Partnership>
        get() {
            val partnerships = mutableListOf<Partnership>()
            val currentPartnership = mutableListOf<BatsmanInnings>()

            // Group balls by over and ball number
            val ballsByOver = oversBowled.flatMap { over ->
                over.balls.mapIndexed { ballIndex, ball ->
                    over.overNumber to (ballIndex + 1) to ball
                }
            }.toMap()

            // Track batsmen at the crease
            var currentBatsmen = batsmenStats.filter { !it.isOut && it.ballsFaced > 0 }

            for ((overBall, ball) in ballsByOver) {
                val (over, ballNum) = overBall

                // Check if there was a wicket on the previous ball
                if (ball.isWicket) {
                    if (currentPartnership.isNotEmpty()) {
                        // Add the completed partnership
                        partnerships.add(
                            Partnership(
                                batsmen = currentPartnership.toList(),
                                runs = currentPartnership.sumOf { it.runs },
                                balls = currentPartnership.maxOfOrNull { it.ballsFaced } ?: 0,
                                startOver = currentPartnership.minOfOrNull { it.entryOver } ?: 0f,
                                endOver = over + (ballNum / 6.0).toFloat()))
                        currentPartnership.clear()
                    }

                    // Add new batsman if any
                    currentBatsmen = batsmenStats.filter { !it.isOut && it.ballsFaced > 0 }
                }

                // Add current batsmen to partnership
                currentBatsmen.forEach { batsman ->
                    if (batsman !in currentPartnership) {
                        currentPartnership.add(batsman)
                    }
                }
            }

            // Add the last partnership if any
            if (currentPartnership.isNotEmpty()) {
                partnerships.add(
                    Partnership(
                        batsmen = currentPartnership.toList(),
                        runs = currentPartnership.sumOf { it.runs },
                        balls = currentPartnership.maxOfOrNull { it.ballsFaced } ?: 0,
                        startOver = currentPartnership.minOfOrNull { it.entryOver } ?: 0f,
                        endOver = overs.toFloat()))
            }

            return partnerships.sortedByDescending { it.runs }
        }

    fun updateBowlerSpell(over: OverOutcome) {
        var spell = bowlerSpells.find { it.bowler == over.bowler }
        if (spell == null) {
            bowlerSpells.add(
                BowlerSpell(
                    over.bowler,
                    over.balls.count(),
                    if (over.runs == 0) 1 else 0,
                    over.runs,
                    over.wickets,
                    over.dots,
                    over.fours,
                    over.sixes,
                    over.wides
                )
            )
        } else {
            val index = bowlerSpells.indexOfLast { it.bowler == over.bowler }
            spell = spell.copy(
                ballsBowled = spell.ballsBowled + over.balls.count(),
                maidens = if (over.runs == 0) spell.maidens + 1 else spell.maidens,
                dots = spell.dots + over.dots,
                runs = spell.runs + over.runs,
                wickets = spell.wickets + over.wickets,
                fours = spell.fours + over.fours,
                sixes = spell.sixes + over.sixes,
                wides = spell.wides + over.wides
            )
            bowlerSpells[index] = spell
        }
    }

    /**
     * Returns the detailed batting card for this innings
     */
    fun battingCard(): String {
        val header = "${"Batsman".padEnd(20)} R   B   4s  6s  SR"
        val separator = "-".repeat(50)

        val batsmen = batsmenStats.sortedBy { it.position }.map { stat ->
            val balls = stat.ballsFaced.coerceAtLeast(0)
            val runs = stat.runs
            val notOut = if (stat.ballsFaced > 0 && !stat.isOut) "*" else " "
            val strikeRate = (runs * 100.0 / balls).toInt()

            "${stat.player.name.padEnd(20)} " + "${notOut}${runs.toString().padEnd(3)} " + "${
                balls.toString().padEnd(3)
            } " + "${stat.fours.toString().padEnd(3)} " + "${
                stat.sixes.toString().padEnd(3)
            } " + "${strikeRate.toString().padEnd(4)} "
        }

        // Add extras and total
        val extrasLine = "Extras: $extras (w $wides, nb $noBalls, lb ${(extras - wides - noBalls).coerceAtLeast(0)})"
        val totalLine = "Total: ${score}/${wickets} (${overs.oversFormatted} overs, RR: ${runRate.runRateFormatted})"

        return (listOf(header, separator) + batsmen + listOf(
            separator, extrasLine, totalLine
        )).joinToString("\n")
    }

    /**
     * Returns the detailed bowling card for this innings
     */
    fun bowlingCard(): String {
        val header = "${"Bowler".padEnd(20)} O    M    R   W    Econ   SR    4w  6w  Dots"
        val separator = "-".repeat(70)

        val bowlers = bowlerSpells.filter { it.ballsBowled > 0 }.sortedBy { it.bowler.name }.map { spell ->
            val overs = spell.oversFormatted
            val economy = if (spell.ballsBowled > 0) {
                String.format("%.2f", spell.runs.toDouble() / (spell.ballsBowled / 6.0))
            } else "0.00"

            val strikeRate = if (spell.wickets > 0) {
                String.format("%.1f", spell.ballsBowled.toDouble() / spell.wickets)
            } else "-"

            val fourWickets = if (spell.wickets >= 4) "1" else "0"
            val sixWickets = if (spell.wickets >= 6) "1" else "0"

            "${spell.bowler.name.padEnd(20)} " + "$overs " + "${
                spell.maidens.toString().padEnd(4)
            } " + "${spell.runs.toString().padEnd(3)} " + "${
                spell.wickets.toString().padEnd(3)
            } " + "${economy.padEnd(6)} " + "${strikeRate.padEnd(6)} " + "${
                fourWickets.padEnd(
                    3
                )
            } " + "${sixWickets.padEnd(3)} " + "${spell.dots.toString().padEnd(5)} "
        }

        // Add summary
        val totalOvers = oversBowled.sumOf { it.balls.size } / 6.0
        val totalMaidens = bowlerSpells.sumOf { it.maidens }
        val totalRuns = bowlerSpells.sumOf { it.runs }
        val totalWickets = bowlerSpells.sumOf { it.wickets }
        val totalBoundaries = bowlerSpells.sumOf { it.fours + it.sixes }
        val totalDots = bowlerSpells.sumOf { it.dots }

        val summary = "Total: ${totalRuns}-${totalWickets} in ${overs.oversFormatted} overs " + "(RR: ${
            String.format(
                "%.2f", totalRuns / totalOvers
            )
        }, " + "Boundaries: $totalBoundaries, " + "Dots: $totalDots, " + "Maidens: $totalMaidens)"

        return (listOf(header, separator) + bowlers + listOf(separator, summary)).joinToString("\n")
    }
}
