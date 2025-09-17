package io.awais.cricket_championship.engine

import io.awais.cricket_championship.engine.entity.BowlingStyle
import io.awais.cricket_championship.engine.entity.Player
import io.awais.cricket_championship.engine.entity.Team
import io.awais.cricket_championship.engine.result.BatsmanInnings
import io.awais.cricket_championship.engine.result.BowlerSpell
import io.awais.cricket_championship.engine.result.Inning
import io.awais.cricket_championship.engine.store.MatchStore
import kotlin.collections.set
import kotlin.random.Random

class InningsSimulator(private val battingTeam: Team, private val bowlingTeam: Team, private val target: Int? = null) {
    private val random = Random.Default
    private val conditions = MatchStore.conditions
    private val inning = Inning(battingTeam, bowlingTeam, totalOvers = conditions.overs, target = target)

    private val battingOrder = battingTeam.getBattingOrder().toMutableList()
    private val bowlers = bowlingTeam.getBowlers().shuffled(random).toMutableList()

    private var striker: Player = battingOrder.first()
    private var nonStriker: Player = battingOrder[1]
    private lateinit var bowler: Player

    val bowlerOvers = mutableMapOf<String, Int>()
    val batsmanStats = mutableMapOf<String, BatsmanInnings>()
    val bowlerStats = mutableMapOf<String, BowlerSpell>()

    fun simulateInnings(): Inning {
        // Initialize batsmen stats
        battingOrder.forEachIndexed { index, it ->
            batsmanStats[it.name] = BatsmanInnings(it, index + 1, 0, 0, false, null, null, 0, 0)
        }

        // Initialize bowler stats
        bowlers.forEach {
            bowlerStats[it.name] = BowlerSpell(it, 0, 0, 0, 0, 0, 0, 0, 0)
        }

        var currentOver = 0

        while (currentOver < inning.totalOvers) {
            if (isInningsComplete()) break

            print("\nPress Enter to simulate next over...")
            readlnOrNull()

            // Select a bowler who hasn't bowled max overs
            bowler = bowlers.firstOrNull {
                bowlerOvers.getOrDefault(it.name, 0) < conditions.maxOversPerBowler
            } ?: break

            val bowlerName = bowler.name

            Commentary.addCommentary("\n$bowlerName to bowl")

            val overOutcome = simulateOver(currentOver)
            inning.oversBowled.add(overOutcome)

            // Add over summary
            Commentary.addOverSummary(overOutcome, inning)

            // Update bowler's over count and add to spells
            bowlerOvers[bowlerName] = bowlerOvers.getOrDefault(bowlerName, 0) + 1
            inning.updateBowlerSpell(overOutcome)

            // Rotate strike at the end of the over
            if (overOutcome.balls.last().runs % 2 == 0) {
                rotateStrike()
            }

            currentOver++
        }

        val lastOver = inning.oversBowled.last()
        val oversCompleted =
            if (lastOver.balls.count() == 6) currentOver else ((currentOver - 1) + (lastOver.balls.count() / 6.0))

        inning.overs = oversCompleted.toDouble()

        return inning
    }

    fun simulateOver(over: Over): OverOutcome {
        var currentBallsInOver = 0
        val ballsBowledInOver = mutableListOf<BallOutcome>()

        val currentOver = OverOutcome(bowler, over, 0, 0, 0, 0, 0, 0, 0)

        while (currentBallsInOver < 6) {
            if (isInningsComplete()) break

            val isPowerplay = conditions.isInPowerplay(currentOver.overNumber)

            val bowlerName = bowler.name
            val bowlerSpell = bowlerStats[bowlerName]!!

            val outcome = simulateBall(isPowerplay)
            ballsBowledInOver.add(outcome)

            // Update batsman and bowler stats
            if (!outcome.isWide && !outcome.isNoBall) {
                val strikerStats = batsmanStats[striker.name]!!
                batsmanStats[striker.name] = strikerStats.copy(
                    ballsFaced = strikerStats.ballsFaced + 1,
                    runs = strikerStats.runs + outcome.runs,
                    isOut = outcome.isWicket,
                    dismissalType = if (outcome.isWicket) outcome.dismissalType else null,
                    fielder = if (outcome.isWicket) outcome.fielder?.name else null,
                    fours = strikerStats.fours + if (outcome.runs == 4) 1 else 0,
                    sixes = strikerStats.sixes + if (outcome.runs == 6) 1 else 0
                )

                bowlerStats[bowler.name] = bowlerStats[bowler.name]!!.copy(
                    ballsBowled = bowlerSpell.ballsBowled + 1,
                    runs = bowlerSpell.runs + outcome.runs,
                    wickets = bowlerSpell.wickets + if (outcome.isWicket) 1 else 0,
                    dots = bowlerSpell.dots + if (outcome.runs == 0) 1 else 0,
                    fours = bowlerSpell.fours + if (outcome.runs == 4) 1 else 0,
                    sixes = bowlerSpell.sixes + if (outcome.runs == 6) 1 else 0,
                    wides = bowlerSpell.wides,
                    noBalls = bowlerSpell.noBalls
                )
            } else {
                inning.extras++
                inning.score++

                bowlerStats[bowler.name] = bowlerStats[bowler.name]!!.copy(
                    runs = bowlerSpell.runs + 1,
                    wides = bowlerSpell.wides + if (outcome.isWide) 1 else 0,
                    noBalls = bowlerSpell.noBalls + if (outcome.isNoBall) 1 else 0
                )

                // For wides and no-balls, the ball is not counted in the over
                currentBallsInOver--
            }

            // Update current spell
            currentOver.runs += outcome.runs
            if (outcome.isWicket) currentOver.wickets++
            if (outcome.runs == 0) currentOver.dots++
            if (outcome.runs == 4) currentOver.fours++
            if (outcome.runs == 6) currentOver.sixes++

            // Add ball commentary
            Commentary.addBallCommentary(outcome, currentOver.overNumber, currentBallsInOver)

            inning.score += outcome.runs

            if (outcome.isWicket) {
                inning.wickets++
                if (battingOrder.isNotEmpty()) {
                    striker = battingOrder[inning.wickets + 1]
                } else {
                    break
                }
            }

            // Rotate strike if needed
            if (outcome.runs % 2 == 1) {
                rotateStrike()
            }

            currentOver.balls.add(outcome)
            currentBallsInOver++
        }

        inning.batsmenStats = batsmanStats.values.toMutableList()
        inning.bowlerSpells = bowlerStats.values.toMutableList()

        return currentOver
    }

    private fun simulateBall(isPowerplay: Boolean): BallOutcome {
        val baseOutProbability = 0.04 // 4% chance of getting out for an average batsman vs average bowler
        val baseBoundaryProbability = 0.1 // 10% chance of hitting a boundary

        val batterRating = striker.battingRating.toDouble() / 100.0
        val bowlerRating = bowler.bowlingRating.toDouble() / 100.0
        val fieldingRating = bowlingTeam.getFieldingAverage() / 100.0

        // Apply conditions
        val weatherFactor = conditions.weather.bowlingImpact
        val pitchFactor = when (bowler.bowlingStyle) {
            BowlingStyle.LEG_SPIN, BowlingStyle.OFF_SPIN, BowlingStyle.ORTHODOX -> conditions.pitchType.spinBowlerImpact
            BowlingStyle.FAST -> conditions.pitchType.paceBowlerImpact
            else -> 1.0
        }

        // Adjust probabilities based on conditions and player ratings
        val outProbability = baseOutProbability * (1 - batterRating * 0.5) * // Better batsmen less likely to get out
                (1 + bowlerRating * 0.5) * // Better bowlers more likely to take wickets
                weatherFactor * pitchFactor

        val boundaryProbability =
            baseBoundaryProbability * (0.7 + batterRating * 0.6) * // Better batsmen more likely to hit boundaries
                    (1 - bowlerRating * 0.3) * // Better bowlers less likely to concede boundaries
                    (1 / weatherFactor) * // Better weather = easier to hit boundaries
                    (1 / pitchFactor) // Better pitch = easier to hit boundaries

        // Check for wicket
        if (random.nextDouble() < outProbability) {
            // Determine type of dismissal
            val isCaught = random.nextDouble() < 0.7 // 70% chance of caught
            val isBowled = !isCaught && random.nextDouble() < 0.5 // 15% chance of bowled

            // Select a random fielder for now
            val fielder = bowlingTeam.players.random()

            return BallOutcome(
                runs = 0, isWicket = true, dismissalType = when {
                    isCaught -> "c ${fielder.name} b ${bowler.name}"
                    isBowled -> "b ${bowler.name}"
                    else -> "lbw b ${bowler.name}" // 15% chance of LBW
                }, batter = striker, bowler = bowler, fielder = if (isCaught) fielder else null
            )
        }

        // Check for wides and no-balls (5% chance combined)
        if (random.nextDouble() < 0.05) {
            val isWide = random.nextBoolean()
            return BallOutcome(
                runs = if (isWide) 1 else 1, // Wides and no-balls give 1 run + any runs scored
                isWide = isWide,
                isNoBall = !isWide,
                batter = striker,
                bowler = bowler,
            )
        }

        // Determine runs scored
        val runs = when {
            random.nextDouble() < boundaryProbability -> {
                // Boundary (4 or 6)
                if (random.nextDouble() < 0.7) 4 else 6 // 70% chance of 4, 30% of 6
            }

            else -> {
                // Regular runs (0-3)
                val runProb = random.nextDouble()
                when {
                    runProb < 0.4 -> 0  // 40% dot ball
                    runProb < 0.75 -> 1 // 35% single
                    runProb < 0.9 -> 2  // 15% double
                    else -> 3           // 10% triple (rare)
                }
            }
        }

        return BallOutcome(
            runs = runs,
            batter = striker,
            bowler = bowler,
        )
    }

    private fun rotateStrike() {
        striker = nonStriker.also { nonStriker = striker }
    }

    private fun isInningsComplete(): Boolean {
        return inning.wickets >= 10 || (target != null && inning.score > target)
    }
}

/**
 * Data class representing the outcome of a single ball
 */
data class BallOutcome(
    val runs: Int,
    val isWicket: Boolean = false,
    val dismissalType: String? = null,
    val isWide: Boolean = false,
    val isNoBall: Boolean = false,
    val isByes: Boolean = false,
    val isLegByes: Boolean = false,
    val batter: Player,
    val bowler: Player,
    val fielder: Player? = null,

    // Additional data for visualizations
    val ballSpeed: Double = Random.nextDouble(120.0, 150.0), // km/h
    val ballLine: Double = Random.nextDouble(-2.0, 2.0), // -2.0 (leg side) to 2.0 (off side)
    val ballLength: Double = Random.nextDouble(0.0, 6.0), // 0.0 (full toss) to 6.0 (bouncer)
    val shotAngle: Int = (0..359).random(), // 0-359 degrees (0 = straight down the ground)
    val shotPower: Double = Random.nextDouble(0.5, 1.0) // 0.5 (defensive) to 1.0 (attacking)
) {
    // Get the shot type based on angle
    val shotType: String = when (shotAngle) {
        in 0..30, in 330..359 -> "Straight"
        in 31..60, in 300..329 -> "On side"
        in 61..120 -> "Square leg"
        in 121..240 -> "Fine leg"
        in 241..300 -> "Third man"
        else -> "Off side"
    }

    // Get the line and length as a string (e.g., "Good length, off stump")
    val lineAndLength: String
        get() {
            val lengthDesc = when {
                ballLength < 1.0 -> "Full toss"
                ballLength < 2.5 -> "Yorker"
                ballLength < 4.0 -> "Full"
                ballLength < 5.0 -> "Good length"
                ballLength < 5.8 -> "Short"
                else -> "Bouncer"
            }

            val lineDesc = when {
                ballLine < -1.5 -> "Wide down leg"
                ballLine < -0.5 -> "Leg stump"
                ballLine < 0.5 -> "Middle stump"
                ballLine < 1.5 -> "Off stump"
                else -> "Wide outside off"
            }

            return "$lengthDesc, $lineDesc"
        }
}

/**
 * Data class representing a bowler's spell in a single over
 */
data class OverOutcome(
    val bowler: Player,
    val overNumber: Int,
    var runs: Int = 0,
    var wickets: Int = 0,
    var dots: Int = 0,
    var fours: Int = 0,
    var sixes: Int = 0,
    var wides: Int = 0,
    var noBalls: Int = 0,
    val balls: MutableList<BallOutcome> = mutableListOf()
)