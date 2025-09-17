package io.awais.cricket_championship.engine.result

import io.awais.cricket_championship.engine.MatchConditions
import io.awais.cricket_championship.engine.entity.Team
import io.awais.cricket_championship.engine.visualization.VisualizationRenderer
import io.awais.cricket_championship.engine.utils.oversFormatted

/**
 * Data class representing the result of a cricket match
 */
data class MatchResult(
    val battingFirstTeam: Team,
    val fieldingFirstTeam: Team,
    val firstInnings: Inning,
    val secondInnings: Inning,
    val conditions: MatchConditions,
    val commentary: List<String>,
    val result: String
) {
    /**
     * Returns a formatted scoreboard of the match
     */
    fun getScoreboard(): String = VisualizationRenderer.renderScoreboard(this)

    /**
     * Returns the match progression visualization
     */
    fun getMatchProgression(): String = VisualizationRenderer.renderMatchProgression(firstInnings, secondInnings)

    /**
     * Returns the wagon wheel visualization for the first innings
     */
    fun getFirstInningsWagonWheel(): String = VisualizationRenderer.renderWagonWheel(firstInnings)

    /**
     * Returns the wagon wheel visualization for the second innings
     */
    fun getSecondInningsWagonWheel(): String = VisualizationRenderer.renderWagonWheel(secondInnings)

    /**
     * Returns the pitch map for the first innings
     */
    fun getFirstInningsPitchMap(): String = VisualizationRenderer.renderPitchMap(firstInnings)

    /**
     * Returns the pitch map for the second innings
     */
    fun getSecondInningsPitchMap(): String = VisualizationRenderer.renderPitchMap(secondInnings)

    /**
     * Returns all visualizations as a single string
     */
    fun getAllVisualizations(): String = """
        ${getScoreboard()}
        ${getMatchProgression()}
        ${getFirstInningsWagonWheel()}
        ${getFirstInningsPitchMap()}
        ${getSecondInningsWagonWheel()}
        ${getSecondInningsPitchMap()}
    """.trimIndent()

    /**
     * Returns a summary of the match result
     */
    fun summary(): String {
        return """
            |=== Match Summary ===
            |${battingFirstTeam.name} ${firstInnings.score}/${firstInnings.wickets} in ${firstInnings.overs.oversFormatted} overs
            |${fieldingFirstTeam.name} ${secondInnings.score}/${secondInnings.wickets} in ${secondInnings.overs.oversFormatted} overs
            |
            |$result
            |
            |${battingFirstTeam.name} Innings:
            |${firstInnings.battingCard()}
            |
            |${fieldingFirstTeam.name} Bowling:
            |${firstInnings.bowlingCard()}
            |
            |${fieldingFirstTeam.name} Innings:
            |${secondInnings.battingCard()}
            |
            |${battingFirstTeam.name} Bowling:
            |${secondInnings.bowlingCard()}
            |
            |Match Conditions: ${conditions.weather}, ${conditions.pitchType}
        """.trimMargin()
    }

    /**
     * Returns the full ball-by-ball commentary
     */
    fun fullCommentary(): String = commentary.joinToString("\n")
}