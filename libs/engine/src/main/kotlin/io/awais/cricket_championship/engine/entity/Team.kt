package io.awais.cricket_championship.engine.entity

/**
 * Data class to represent a cricket team with its players.
 * @param name The name of the team.
 * @param players List of players in the team (should be 11 players).
 */
data class Team(val name: String, val players: List<Player>) {
    init {
        require(players.size == 11) { "A cricket team must have exactly 11 players" }
    }

    /**
     * Gets the team's batting order based on player roles.
     * @return List of players in their suggested batting order.
     */
    fun getBattingOrder(): List<Player> {
        return players.sortedBy { it.battingPosition }
    }

    fun getBowlers(): List<Player> {
        return players.sortedByDescending { it.bowlingRating }.take(5)
    }

    /**
     * Gets the team's wicket-keeper.
     * @return The wicket-keeper player.
     */
    fun getWicketKeeper(): Player {
        return players.first { it.isWicketKeeper() }
    }

    /**
     * Calculates the team's overall rating based on player ratings.
     * @return Team rating (1-100).
     */
    fun calculateTeamRating(): Int {
        val battingAvg = players.map { it.battingRating }.average().toInt()
        val bowlingAvg = players.map { it.bowlingRating }.take(5).average().toInt()
        return ((battingAvg * 0.6) + (bowlingAvg * 0.4)).toInt().coerceIn(1, 100)
    }

    fun getFieldingAverage(): Int {
        return players.map { it.fieldingRating }.average().toInt()
    }

    fun getBestFielder(): Player {
        return players.minByOrNull { it.fieldingRating }!!
    }
}
