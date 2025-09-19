package io.awais.cricket_championship.console

/**
 * Main entry point for the Cricket Simulation Game
 */
fun main() {
    println("=== Welcome to Cricket Championship: Simulator ===\n")

    val cli = SimulatorCLI()

    try {
        cli.start()
    } catch (e: Exception) {
        println("\nAn error occurred: ${e.message}")
        e.printStackTrace()
    }

    println("\n=== Thank you for playing Cricket Championship: Simulator === ")
}
