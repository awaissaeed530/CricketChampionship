package io.awais.cricket_championship.engine.utils

import kotlin.math.roundToInt

/**
 * Extension function to format overs as a string (e.g., 12.3)
 */
val Double.oversFormatted: String
    get() {
        val overs = toInt()
        val balls = ((this - overs) * 10).roundToInt()
        return "$overs.${balls}"
    }

/**
 * Extension function to format run rate to 2 decimal places
 */
val Double.runRateFormatted: String
    get() = String.format("%.2f", this)