package io.awais.cricket_championship.engine

import io.awais.cricket_championship.engine.entity.*
import java.io.File

object DataLoader {
    private const val DATA_DIR = "/data"
    private const val SHORT_TEAMS_DIR = "$DATA_DIR/teams/"
    private const val TEAMS_DIR = "libs/engine/src/main/resources$SHORT_TEAMS_DIR"

    fun loadAllTeams(): Map<String, Team> {
        return try {
            val teamsDir = File(TEAMS_DIR)
            if (!teamsDir.exists() || !teamsDir.isDirectory) {
                throw IllegalStateException("Teams directory not found: ${teamsDir.absolutePath}")
            }

            teamsDir.listFiles { _, name -> name.endsWith(".csv") }?.associate { file ->
                val teamName = file.nameWithoutExtension
                teamName to loadTeam(teamName)
            }!!
        } catch (e: Exception) {
            println("Error loading teams: ${e.message}")
            emptyMap()
        }
    }

    fun loadTeam(teamName: String): Team {
        val inputStream = object {}.javaClass.getResourceAsStream("$SHORT_TEAMS_DIR$teamName.csv")
            ?: throw IllegalArgumentException("Team file not found: $teamName.csv")

        return try {
            val players = inputStream.bufferedReader().useLines { lines ->
                lines.drop(1) // Skip header
                    .map { parsePlayer(it) }.filterNotNull().toList()
            }

            if (players.size != 11) {
                throw IllegalArgumentException("Team $teamName must have exactly 11 players, found ${players.size}")
            }

            Team(teamName, players)
        } catch (e: Exception) {
            throw RuntimeException("Failed to load team $teamName: ${e.message}", e)
        }
    }

    private fun parsePlayer(line: String): Player? {
        if (line.isBlank()) return null

        val parts = line.split(",")
        if (parts.size < 9) return null

        return try {
            Player(
                name = parts[0],
                role = PlayerRole.valueOf(parts[1]),
                battingPosition = parts[3].toInt(),
                hand = Hand.valueOf(parts[2]),
                battingRating = parts[4].toInt(),
                bowlingRating = parts[5].toInt(),
                bowlingStyle = BowlingStyle.valueOf(parts[6]),
                fieldingRating = parts[7].toInt(),
                fitness = parts[8].toInt(),
                form = parts[9].toDouble()
            )
        } catch (e: Exception) {
            println("Error parsing player data: $line - ${e.message}")
            null
        }
    }
}
