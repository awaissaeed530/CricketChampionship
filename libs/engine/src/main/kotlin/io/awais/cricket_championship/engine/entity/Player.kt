package io.awais.cricket_championship.engine.entity

/**
 * Represents a player's role in the team.
 */
enum class PlayerRole {
    OPENER, TOP_ORDER, MIDDLE_ORDER, ALL_ROUNDER, BOWLING_ALL_ROUNDER, WICKET_KEEPER, BOWLER,
}

/**
 * Represents a player's batting or bowling hand.
 */
enum class Hand {
    RIGHT, LEFT
}

/**
 * Represents a player's bowling style.
 */
enum class BowlingStyle {
    FAST, MEDIUM, OFF_SPIN, LEG_SPIN, ORTHODOX,
}

/**
 * Data class representing a cricket player.
 * @param name Player's name
 * @param role Player's role in the team
 * @param hand Player's preferred hand
 * @param bowlingStyle Player's bowling style (null if not a bowler)
 * @param battingRating Batting skill (1-100)
 * @param bowlingRating Bowling skill (1-100, 0 if not a bowler)
 * @param fieldingRating Fielding skill (1-100)
 * @param fitness Current fitness level (1-100)
 * @param form Current form (0.5-1.5 multiplier)
 */
data class Player(
    val name: String,
    val role: PlayerRole,
    val battingPosition: Int,
    val hand: Hand,
    val bowlingStyle: BowlingStyle,
    val battingRating: Int,
    val bowlingRating: Int,
    val fieldingRating: Int = 50,
    val fitness: Int = 100,
    val form: Double = 1.0
) {
    init {
        require(battingRating in 1..100) { "Batting rating must be between 1-100" }
        require(bowlingRating in 0..100) { "Bowling rating must be between 0-100" }
        require(fieldingRating in 1..100) { "Fielding rating must be between 1-100" }
        require(fitness in 1..100) { "Fitness must be between 1-100" }
        require(form in 0.5..1.5) { "Form must be between 0.5-1.5" }
    }

    /**
     * Creates a new player with updated form.
     * @param newForm New form value (0.5-1.5)
     * @return A new Player instance with updated form
     */
    fun withForm(newForm: Double): Player {
        require(newForm in 0.5..1.5) { "Form must be between 0.5-1.5" }
        return copy(form = newForm)
    }

    /**
     * Creates a new player with updated fitness.
     * @param newFitness New fitness value (1-100)
     * @return A new Player instance with updated fitness
     */
    fun withFitness(newFitness: Int): Player {
        require(newFitness in 1..100) { "Fitness must be between 1-100" }
        return copy(fitness = newFitness)
    }

    /**
     * Calculates the fitness factor.
     */
    fun fitnessFactor(): Double = fitness / 100.0

    /**
     * Calculates the effective batting rating considering form and fitness.
     */
    fun effectiveBattingRating(): Double {
        return battingRating * form * fitnessFactor()
    }

    /**
     * Calculates the effective bowling rating considering form and fitness.
     */
    fun effectiveBowlingRating(): Double {
        return bowlingRating * form * fitnessFactor()
    }

    /**
     * Checks if the player is a wicket-keeper.
     */
    fun isWicketKeeper(): Boolean = role == PlayerRole.WICKET_KEEPER
}