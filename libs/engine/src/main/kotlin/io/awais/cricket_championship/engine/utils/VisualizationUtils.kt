package io.awais.cricket_championship.engine.utils

/**
 * Utility functions for visualization
 */
object VisualizationUtils {
    /**
     * Creates a simple bar chart from a map of values
     */
    fun createBarChart(
        data: Map<String, Int>, maxWidth: Int = 50, showValues: Boolean = true
    ): String {
        if (data.isEmpty()) return "No data available"

        val maxValue = data.values.maxOrNull()?.toDouble() ?: 0.0
        val scale = if (maxValue > 0) maxWidth / maxValue else 1.0

        return buildString {
            data.entries.sortedByDescending { it.value }.forEach { (label, value) ->
                val barLength = (value * scale).toInt().coerceAtLeast(1)
                val bar = "■".repeat(barLength)
                val valueText = if (showValues) " $value" else ""
                appendLine("${label.padEnd(15)}: $bar$valueText")
            }
        }
    }

    /**
     * Creates a simple line chart from a list of values
     */
    fun createLineChart(
        values: List<Int>, width: Int = 50, height: Int = 10, showAxes: Boolean = true
    ): String {
        if (values.isEmpty()) return "No data available"

        val maxValue = values.maxOrNull()?.toDouble() ?: 1.0
        val minValue = values.minOrNull()?.toDouble() ?: 0.0
        val range = (maxValue - minValue).coerceAtLeast(1.0)

        val xStep = (values.size - 1).coerceAtLeast(1).toDouble() / (width - 1)
        val yStep = range / (height - 1)

        val chart = MutableList(height) { CharArray(width) { ' ' } }

        // Draw axes if needed
        if (showAxes) {
            // X-axis
            for (x in 0 until width) {
                chart[height - 1][x] = '─'
            }

            // Y-axis
            for (y in 0 until height) {
                chart[y][0] = '│'
            }

            // Origin
            chart[height - 1][0] = '└'
        }

        // Draw line
        for (i in 0 until width) {
            val x = i * xStep
            val idx = x.toInt().coerceIn(values.indices)
            val value = values[idx].toDouble()
            val y = ((value - minValue) / range * (height - 1)).toInt()
            val yPos = (height - 1 - y).coerceIn(0, height - 1)

            if (chart[yPos][i] == ' ') {
                chart[yPos][i] = '•'
            }
        }

        // Add labels
        val result = StringBuilder()
        chart.forEachIndexed { y, row ->
            result.append(row.joinToString(""))
            if (showAxes && y == 0) {
                result.append(" ${maxValue.toInt()}")
            } else if (showAxes && y == height - 1) {
                result.append(" ${minValue.toInt()}")
            }
            result.appendLine()
        }

        // Add X-axis labels
        if (showAxes) {
            result.append(" ".repeat(width + 1)).appendLine("0")
            result.append(" ".repeat(width - 1)).appendLine("${values.size - 1} (overs)")
        }

        return result.toString()
    }

    /**
     * Creates a simple table from a list of rows
     */
    fun createTable(
        headers: List<String>, rows: List<List<Any>>, padding: Int = 1
    ): String {
        if (headers.isEmpty() || rows.isEmpty()) return ""

        val columnWidths = headers.map { it.length }.toMutableList()

        // Calculate max width for each column
        rows.forEach { row ->
            row.forEachIndexed { index, cell ->
                val cellLength = cell.toString().length
                if (cellLength > columnWidths[index]) {
                    columnWidths[index] = cellLength
                }
            }
        }

        val sb = StringBuilder()

        // Add top border
        sb.append("+")
        columnWidths.forEach { width ->
            sb.append("-".repeat(width + padding * 2)).append("+")
        }
        sb.appendLine()

        // Add headers
        sb.append("|")
        headers.forEachIndexed { index, header ->
            sb.append(" ".repeat(padding)).append(header.padEnd(columnWidths[index])).append(" ".repeat(padding))
                .append("|")
        }
        sb.appendLine()

        // Add separator
        sb.append("+")
        columnWidths.forEach { width ->
            sb.append("-".repeat(width + padding * 2)).append("+")
        }
        sb.appendLine()

        // Add rows
        rows.forEach { row ->
            sb.append("|")
            row.forEachIndexed { index, cell ->
                sb.append(" ".repeat(padding)).append(cell.toString().padEnd(columnWidths[index]))
                    .append(" ".repeat(padding)).append("|")
            }
            sb.appendLine()
        }

        // Add bottom border
        sb.append("+")
        columnWidths.forEach { width ->
            sb.append("-".repeat(width + padding * 2)).append("+")
        }

        return sb.toString()
    }
}
