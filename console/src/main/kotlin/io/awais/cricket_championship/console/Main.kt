package io.awais.cricket_championship.console

/**
 * Main entry point for the Cricket Simulation Game
 */
fun main() {
    println("=== Welcome to Cricket Simulator ===\n")

    val cli = InteractiveGameCLI()
    
    try {
        cli.start()
    } catch (e: Exception) {
        println("\nAn error occurred: ${e.message}")
        e.printStackTrace()
    }
    
    println("\nThank you for playing Cricket Simulation!")
}
