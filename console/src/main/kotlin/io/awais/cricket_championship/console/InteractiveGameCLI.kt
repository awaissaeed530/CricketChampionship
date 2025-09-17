package io.awais.cricket_championship.console

import io.awais.cricket_championship.engine.*
import io.awais.cricket_championship.engine.entity.Team
import io.awais.cricket_championship.engine.store.MatchStore
import java.util.*

/**
 * Command-line interface for the interactive cricket game
 */
class InteractiveGameCLI() {
    private val engine = MatchEngine()
    private val scanner = Scanner(System.`in`)
    private val availableTeams = mutableListOf<Team>()

    /**
     * Load all available teams from the teams directory
     */
    fun loadTeams() {
        TeamLoader.loadAllTeams().forEach { availableTeams.add(it.value) }

        if (availableTeams.size < 2) {
            throw IllegalStateException("At least 2 teams are required to start a match")
        }
    }

    /**
     * Get list of available team names
     */
    fun getAvailableTeamNames(): List<String> = availableTeams.map { it.name }

    /**
     * Start the interactive game
     */
    fun start() {
        loadTeams()
        println("Teams loaded successfully!")

        selectTeam()
        performToss()

        playMatch()
    }

    private fun selectTeam() {
        val teamNames = getAvailableTeamNames()
        println("\n=== Select Your Team ===")
        teamNames.forEachIndexed { index, name ->
            println("${index + 1}. $name")
        }

        var selectedIndex: Int
        do {
            print("\nEnter team number: ")
            selectedIndex = readInt(1, teamNames.size) - 1
        } while (selectedIndex !in teamNames.indices)

        val teamName = teamNames[selectedIndex]
        val userTeam = availableTeams.firstOrNull { it.name.equals(teamName, ignoreCase = true) }
            ?: throw IllegalArgumentException("Team not found: $teamName")

        // Set opponent as a random team that's not the user's team
        val opponentTeam = availableTeams.filter { it != userTeam }.random()

        engine.selectTeams(userTeam, opponentTeam)
        println("\nYou have selected: $teamName")
    }

    private fun performToss() {
        println("\n=== Toss Time! ===")
        var call: String
        do {
            print("Call heads or tails (h/t): ")
            val input = scanner.next().trim().lowercase()
            call = when (input) {
                "h", "heads" -> "heads"
                "t", "tails" -> "tails"
                else -> {
                    println("Please enter 'h' for heads or 't' for tails")
                    ""
                }
            }
        } while (call.isEmpty())

        val (result, won) = engine.tossCoin(call)
        println("\nThe coin lands on... $result!")

        if (won) {
            println("You won the toss!")
            var choice: String
            do {
                print("Do you want to bat or bowl first? (bat/bowl): ")
                val input = scanner.next().trim().lowercase()
                choice = when (input) {
                    "bat", "b" -> "bat"
                    "bowl", "o" -> "bowl"
                    else -> {
                        println("Please enter 'bat' or 'bowl'")
                        ""
                    }
                }
            } while (choice.isEmpty())

            engine.setTossChoice(choice)
            println("You chose to $choice first!")
        } else {
            println("You lost the toss!")
            val opponentChoice = if (Random().nextBoolean()) "bat" else "bowl"
            engine.setTossChoice(opponentChoice)
            println("Opponent chose to $opponentChoice first!")
        }
    }

    private fun playMatch() {
        println("\n=== Match Starts! ===")
        MatchStore.conditions = MatchConditions()

        engine.simulateMatch()
        println("\n=== Match Complete! ===")
    }

    private fun readInt(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int {
        while (true) {
            try {
                val input = scanner.nextInt()
                if (input in min..max) {
                    return input
                }
                println("Please enter a number between $min and $max")
            } catch (e: InputMismatchException) {
                println("Please enter a valid number")
                scanner.next() // consume the invalid input
            }
        }
    }
}
