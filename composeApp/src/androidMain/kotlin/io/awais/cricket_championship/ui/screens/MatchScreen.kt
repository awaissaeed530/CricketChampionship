package io.awais.cricket_championship.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun MatchScreen(
    onMatchComplete: () -> Unit, onBack: () -> Unit
) {
    var showScorecard by remember { mutableStateOf(false) }
    var currentBatsman by remember { mutableStateOf("Player 1") }
    var currentBowler by remember { mutableStateOf("Bowler 1") }
    var score by remember { mutableStateOf(0) }
    var wickets by remember { mutableStateOf(0) }
    var overs by remember { mutableStateOf(0.0) }
    var target by remember { mutableStateOf<Int?>(180) }
    var isMatchComplete by remember { mutableStateOf(false) }
    var commentary by remember { mutableStateOf(listOf("Match about to begin!")) }

    // Simulate match progress
    LaunchedEffect(Unit) {
        // Simulate ball-by-ball progress
        repeat(120) { ball ->
            delay(1000) // Simulate time between balls

            // Update score, wickets, overs
            val runs = (0..6).random()
            if (runs == 5) { // Wicket
                wickets++
                if (wickets >= 10) {
//                    delay(2000)
//                    onMatchComplete()
                    return@LaunchedEffect
                }
                commentary = commentary + listOf("OUT! $currentBatsman is out! $score/$wickets")
                currentBatsman = "Player ${wickets + 2}"
            } else {
                score += runs
                commentary = commentary + listOf("${runs} run${if (runs != 1) "s" else ""} scored. $score/$wickets")

                // Check if target is reached
                target?.let {
                    if (score > it) {
                        isMatchComplete = true
                        commentary = commentary + listOf("Team wins by ${10 - wickets} wickets!")
                        delay(2000)
                        onMatchComplete()
                        return@LaunchedEffect
                    }
                }
            }

            // Update overs (6 balls per over)
            overs = (ball + 1) / 6.0

            // Change bowler every over
            if ((ball + 1) % 6 == 0) {
                currentBowler = if (currentBowler == "Bowler 1") "Bowler 2" else "Bowler 1"
                commentary = commentary + listOf("End of over ${(ball + 1) / 6}. $score/$wickets")
            }

            // End match after 20 overs or all out
            if (ball == 119) {
                isMatchComplete = true
                target?.let {
                    val result = when {
                        score > it -> "Team wins by ${10 - wickets} wickets!"
                        score == it -> "Match tied!"
                        else -> "Team lost by ${it - score} runs"
                    }
                    commentary = commentary + listOf("Match over! $result")
                } ?: run {
                    commentary = commentary + listOf("Innings complete! $score/$wickets")
                }
                delay(2000)
                onMatchComplete()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top bar
        TopAppBar(title = { Text("Match In Progress") }, navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = "Back"
                )
            }
        }, actions = {
            IconButton(onClick = { showScorecard = !showScorecard }) {
                Icon(
                    imageVector = if (showScorecard) Icons.AutoMirrored.Outlined.ExitToApp else Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = if (showScorecard) "Hide Scorecard" else "Show Scorecard"
                )
            }
        })

        // Scoreboard
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (target != null) "TARGET: $target" else "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "$score/$wickets",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Runs/Wickets",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = String.format("%.1f", overs),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Overs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.Start) {
                        val runRate = if (overs > 0) String.format("%.2f", score / overs) else "0.00"
                        Text(
                            text = runRate,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Run Rate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                // Required run rate if chasing
                target?.let {
                    val remaining = it - score
                    val ballsRemaining = 120 - (overs * 6).toInt()
                    val requiredRunRate = if (ballsRemaining > 0) String.format(
                        "%.2f", remaining.toDouble() * 6 / ballsRemaining
                    ) else "-"

                    Text(
                        "Req. ${remaining}r from ${ballsRemaining}b (RR: $requiredRunRate)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Current batsmen and bowler
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Batting",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    currentBatsman,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Player 2",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Bowling",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    currentBowler,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${String.format("%.1f", overs % 6)}.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Commentary or Scorecard
        if (showScorecard) {
            // Simple scorecard view
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    "Scorecard", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp)
                )

                // Batsmen
                Text(
                    "Batting",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // This would be populated with actual batsmen data
                repeat(3) { i ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Player ${i + 1} ${if (i == 0) "*" else ""}")
                        Text("${(20..80).random()} (${(20..60).random()}b)")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bowlers
                Text(
                    "Bowling",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // This would be populated with actual bowlers data
                repeat(2) { i ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bowler ${i + 1}${if (i == 0) " *" else ""}")
                        Text("${(0..3).random()}/${(0..40).random()}")
                    }
                }
            }
        } else {
            // Commentary view
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Commentary",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f), reverseLayout = true
                ) {
                    items(commentary.reversed()) { comment ->
                        Text(
                            text = comment,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Match controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // These would be interactive controls in a real implementation
            Button(onClick = { /* Simulate boundary */ }) {
                Text("4")
            }

            Button(onClick = { /* Simulate single */ }) {
                Text("1")
            }

            Button(onClick = { /* Simulate wicket */ }) {
                Text("W")
            }

            Button(onClick = { /* Simulate dot ball */ }) {
                Text("0")
            }
        }
    }
}

// Preview for the MatchScreen
@Composable
fun MatchScreenPreview() {
    MaterialTheme {
        MatchScreen(onMatchComplete = {}, onBack = {})
    }
}
