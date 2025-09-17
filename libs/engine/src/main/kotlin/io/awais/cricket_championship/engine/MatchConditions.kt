package io.awais.cricket_championship.engine

/**
 * Type alias for over number in a cricket match
 */
typealias Over = Int

/**
 * Data class representing powerplay fielding restrictions
 * @property maxFieldersOutsideCircle Maximum fielders allowed outside the 30-yard circle
 * @property maxFieldersInCircle Maximum fielders allowed inside the 30-yard circle
 * @property startOver Starting over of this powerplay phase (inclusive)
 * @property endOver Ending over of this powerplay phase (exclusive)
 */
data class PowerplayRule(
    val maxFieldersOutsideCircle: Int,
    val maxFieldersInCircle: Int = 5 - maxFieldersOutsideCircle,
    val startOver: Over = 0,
    val endOver: Over
)

/**
 * Enum representing different cricket match formats with their specific rules
 * @property displayName User-friendly display name
 * @property maxOvers Maximum overs per innings for this format
 */
enum class MatchFormat(val displayName: String, val maxOvers: Int) {
    T20("Twenty20", 20) {
        override val powerplayRules = listOf(
            PowerplayRule(maxFieldersOutsideCircle = 2, endOver = 6)
        )
        override val maxOversPerBowler = 4
        override val innings = 2
    },

    ODI("One Day International", 50) {
        override val powerplayRules = listOf(
            PowerplayRule(maxFieldersOutsideCircle = 2, endOver = 10),
            PowerplayRule(maxFieldersOutsideCircle = 3, startOver = 11, endOver = 40)
        )
        override val maxOversPerBowler = 10
        override val innings = 2
    },

    TEST("Test Match", 90) {  // 90 overs per day
        override val powerplayRules = listOf<PowerplayRule>()
        override val maxOversPerBowler = Int.MAX_VALUE  // No limit in Tests
        override val innings = 4
    };

    abstract val powerplayRules: List<PowerplayRule>
    abstract val maxOversPerBowler: Int
    abstract val innings: Int

    /**
     * Check if a given over is in powerplay
     * @param over The over number to check
     * @return true if the over is in powerplay, false otherwise
     */
    fun isInPowerplay(over: Over): Boolean {
        return powerplayRules.any { over in it.startOver until it.endOver }
    }

    /**
     * Get the powerplay rules for a specific over
     * @param over The over number to check
     * @return PowerplayRules if the over is in powerplay, null otherwise
     */
    fun getPowerplayRules(over: Over): PowerplayRule? {
        return powerplayRules.find { over in it.startOver until it.endOver }
    }
}

/**
 * Enum representing weather conditions and their effects on the match
 * @property battingImpact Multiplier for batting performance (1.0 = normal)
 * @property bowlingImpact Multiplier for bowling performance (1.0 = normal)
 * @property description Description of the weather conditions
 */
enum class Weather(
    val battingImpact: Double, val bowlingImpact: Double, val description: String
) {
    SUNNY(1.0, 1.0, "Clear and sunny conditions"), CLOUDY(
        0.95,
        1.1,
        "Cloudy conditions, good for swing bowling"
    ),
    OVERCAST(0.9, 1.2, "Overcast, excellent bowling conditions"), RAINY(
        0.8,
        1.0,
        "Rain affecting play, conditions favor bowlers"
    ),
    DRIZZLE(0.85, 1.1, "Light rain, conditions favor bowlers"), HOT(1.1, 0.9, "Hot weather, good for batting"), HUMID(
        1.0,
        1.15,
        "Humid conditions, helps swing bowlers"
    );

    override fun toString(): String = description
}

/**
 * Enum representing different pitch types and their characteristics.
 * @property battingEase Multiplier for batting ease (1.0 = normal)
 * @property paceBowlerImpact Multiplier for pace bowlers' effectiveness
 * @property spinBowlerImpact Multiplier for spin bowlers' effectiveness
 * @property description Description of the pitch conditions
 */
enum class PitchType(
    val battingEase: Double, val paceBowlerImpact: Double, val spinBowlerImpact: Double, val description: String
) {
    DRY(0.9, 0.8, 1.3, "Dry surface, helps spinners as the game progresses"), DUSTY(
        0.85,
        0.75,
        1.4,
        "Dusty pitch, offers significant turn for spinners"
    ),
    GREEN(0.8, 1.4, 0.7, "Green top, excellent for fast bowlers with seam and swing"), NORMAL(
        1.0,
        1.0,
        1.0,
        "Well-balanced pitch with something for everyone"
    ),
    FLAT(1.3, 0.6, 0.8, "Flat track, batsman's paradise with true bounce"), CRACKED(
        0.7,
        1.3,
        1.2,
        "Cracked surface, unpredictable bounce favors bowlers"
    );

    override fun toString(): String = description
}

/**
 * Data class representing the conditions for a cricket match.
 * @property format The format of the match (T20, ODI, Test)
 * @property weather Current weather conditions
 * @property pitchType Type of pitch
 * @property homeTeam Name of the home team (for home advantage)
 * @property overs Number of overs per innings (defaults to format's maxOvers)
 * @property powerplayOvers Number of powerplay overs (defaults to format's first powerplay)
 * @property maxOversPerBowler Maximum overs per bowler (defaults to format's maxOversPerBowler)
 */
data class MatchConditions(
    val format: MatchFormat = MatchFormat.T20,
    val weather: Weather = Weather.SUNNY,
    val pitchType: PitchType = PitchType.NORMAL,
    val homeTeam: String = "",
    val overs: Int = format.maxOvers,
    val powerplayOvers: Int = format.powerplayRules.firstOrNull()?.let { it.endOver - it.startOver } ?: 0,
    val maxOversPerBowler: Int = format.maxOversPerBowler
) {
    init {
        require(overs in 1..format.maxOvers) {
            "Overs must be between 1 and ${format.maxOvers} for ${format.displayName} format"
        }
    }

    /**
     * Check if a given over is in powerplay
     * @param over The over number to check
     * @return true if the over is in powerplay, false otherwise
     */
    fun isInPowerplay(over: Over): Boolean = format.isInPowerplay(over)

    /**
     * Get the powerplay rules for a specific over
     * @param over The over number to check
     * @return PowerplayRules if the over is in powerplay, null otherwise
     */
    fun getPowerplayRules(over: Over): PowerplayRule? = format.getPowerplayRules(over)

    /**
     * Get the home advantage multiplier for a team
     * @param teamName Name of the team to check
     * @return 1.1 if the team is home team, 1.0 otherwise
     */
    fun getHomeAdvantage(teamName: String): Double = if (teamName == homeTeam) 1.1 else 1.0
}
