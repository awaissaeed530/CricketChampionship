package io.awais.cricket_championship.engine

import io.awais.cricket_championship.engine.result.Inning
import io.awais.cricket_championship.engine.result.MatchResult
import io.awais.cricket_championship.engine.utils.oversFormatted
import io.awais.cricket_championship.engine.utils.runRateFormatted
import io.awais.cricket_championship.engine.entity.Team
import kotlin.random.Random

/**
 * Enhanced MatchEngine that provides over-by-over simulation with ball-by-ball commentary
 * and support for different cricket formats.
 */
class MatchEngine(private val conditions: MatchConditions = MatchConditions()) {
    private lateinit var userTeam: Team
    private lateinit var opponentTeam: Team
    private lateinit var battingFirst: Team
    private lateinit var bowlingFirst: Team
    private var userWonToss = false
    private var matchComplete = false
    private var currentInnings: Inning? = null

    /**
     * Select user's team
     */
    fun selectTeams(user: Team, opponent: Team) {
        userTeam = user
        opponentTeam = opponent
    }

    /**
     * Toss a coin and check if user's call was correct
     * @param call Either "heads" or "tails"
     * @return Pair of (tossResult: String, userWon: Boolean)
     */
    fun tossCoin(call: String): Pair<String, Boolean> {
        require(call.equals("heads", ignoreCase = true) || call.equals("tails", ignoreCase = true)) {
            "Call must be either 'heads' or 'tails'"
        }

        val tossResult = if (Random.nextBoolean()) "heads" else "tails"
        userWonToss = tossResult.equals(call, ignoreCase = true)
        return tossResult to userWonToss
    }

    /**
     * Set batting or bowling choice after winning the toss
     * @param choice Either "bat" or "bowl"
     */
    fun setTossChoice(choice: String) {
        require(choice.equals("bat", ignoreCase = true) || choice.equals("bowl", ignoreCase = true)) {
            "Choice must be either 'bat' or 'bowl'"
        }

        val userBattingFirst =
            if (userWonToss && choice.equals("bat", ignoreCase = true)) true else if (!userWonToss && choice.equals(
                    "bowl", ignoreCase = true
                )
            ) true else false

        battingFirst = if (userBattingFirst) userTeam else opponentTeam
        bowlingFirst = if (userBattingFirst) opponentTeam else userTeam
    }

    /**
     * Simulates a complete cricket match between two teams
     * @param battingFirst First team in the match
     * @param bowlingFirst Second team in the match
     * @return MatchResult containing the details of the match
     */
    fun simulateMatch(): MatchResult {
        // First Innings
        Commentary.addCommentary("\n=== ${battingFirst.name} Innings ===")
        val firstInnings = InningsSimulator(battingFirst, bowlingFirst).simulateInnings()
        val target = firstInnings.score + 1

        Commentary.addCommentary("\n${battingFirst.name} scored ${firstInnings.score}/${firstInnings.wickets} in ${firstInnings.overs.oversFormatted}")
        Commentary.addCommentary("${bowlingFirst.name} need $target runs to win\n")

        // Second Innings
        Commentary.addCommentary("=== ${bowlingFirst.name} Innings ===")
        val secondInnings = InningsSimulator(bowlingFirst, battingFirst, target).simulateInnings()

        // Match Result
        val result = when {
            secondInnings.score >= target -> "${bowlingFirst.name} won by ${10 - secondInnings.wickets} wickets"
            secondInnings.score == target - 1 -> "Match tied"
            else -> "${battingFirst.name} won by ${target - 1 - secondInnings.score} runs"
        }

        Commentary.addCommentary("\n=== Match Result ===")
        Commentary.addCommentary(result)

        Commentary.addCommentary("\n=== First Innings Scorecard ===")
        Commentary.addCommentary(firstInnings.battingCard())
        Commentary.addCommentary(firstInnings.bowlingCard())

        Commentary.addCommentary("\n=== Second Innings Scorecard ===")
        Commentary.addCommentary(secondInnings.battingCard())
        Commentary.addCommentary(secondInnings.bowlingCard())

        return MatchResult(
            battingFirstTeam = battingFirst,
            fieldingFirstTeam = bowlingFirst,
            firstInnings = firstInnings,
            secondInnings = secondInnings,
            conditions = conditions,
            commentary = Commentary.commentary,
            result = result
        )
    }

    /**
     * Get the current scorecard
     */
    fun getScorecard(): String {
        val current = currentInnings ?: return "Match not started yet"

        return """
            |${current.battingTeam.name}: ${current.score}/${current.wickets} in ${current.overs.oversFormatted}
            |Run Rate: ${current.runRate.runRateFormatted} | Required Run Rate: ${current.requiredRunRate?.runRateFormatted}
            |${if (matchComplete) "Match Complete" else "In Progress"}
            """.trimMargin()
    }
}
