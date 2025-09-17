package io.awais.cricket_championship.engine

import io.awais.cricket_championship.engine.result.Inning
import io.awais.cricket_championship.engine.utils.runRateFormatted

object Commentary {
    val commentary = mutableListOf<String>()

    fun addCommentary(comment: String) {
        commentary.add(comment)
        println(comment)
    }

    fun addBallCommentary(outcome: BallOutcome, over: Int, ball: Int) {
        val overBall = "$over.${ball + 1}"
        val bowlerName = outcome.bowler.name
        val batterName = outcome.batter.name

        val comment = when {
            outcome.isWicket -> {
                val dismissal = when {
                    outcome.dismissalType?.startsWith("c ") == true -> "Caught by ${outcome.fielder?.name}!!"
                    outcome.dismissalType?.startsWith("b ") == true -> "Bowled him!"
                    outcome.dismissalType?.startsWith("lbw") == true -> "LBW! That's plumb in front!"
                    else -> "OUT!"
                }
                "$overBall - $bowlerName to $batterName, OUT! $dismissal ${outcome.dismissalType}"
            }

            outcome.isWide -> "$overBall - $bowlerName to $batterName, WIDE"
            outcome.isNoBall -> "$overBall - $bowlerName to $batterName, NO BALL"
            outcome.runs == 4 -> "$overBall - $bowlerName to $batterName, FOUR! Beautiful shot!"
            outcome.runs == 6 -> "$overBall - $bowlerName to $batterName, SIX! That's gone all the way!"
            outcome.runs > 0 -> "$overBall - $bowlerName to $batterName, ${outcome.runs} run${if (outcome.runs > 1) "s" else ""}"
            else -> "$overBall - $bowlerName to $batterName, dot ball"
        }

        addCommentary(comment)
    }

    fun addOverSummary(over: OverOutcome, inning: Inning) {
        val summary = buildString {
            appendLine("\n========")

            appendLine("End of over ${over.overNumber + 1} - ${over.runs} runs ${if (over.wickets > 0) "& ${over.wickets} wickets" else ""}")

            append("${inning.battingTeam.name}: ${inning.score}/${inning.wickets} - ")
            appendLine("RR: ${inning.runRate.runRateFormatted}")

            if (inning.target != null) {
                append("RRR: ${inning.requiredRunRate?.runRateFormatted} . ")
                appendLine("Need ${inning.target - inning.score} run from ${inning.totalOvers * 6 - inning.remainingBalls} balls")
            }

            // Display last few balls
            appendLine("Last 6 balls:")
            over.balls.forEach { ball ->
                val outcome = when {
                    ball.isWicket -> "W"
                    ball.isWide -> "Wd"
                    ball.isNoBall -> "Nb"
                    ball.runs > 0 -> ball.runs.toString()
                    else -> "."
                }
                append("$outcome ")
            }
        }

        addCommentary(summary)
    }
}