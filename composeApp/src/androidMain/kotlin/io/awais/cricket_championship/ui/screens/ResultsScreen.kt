package io.awais.cricket_championship.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    // In a real app, this would come from the ViewModel/match engine
    val matchResult = remember {
        MatchResult(
            team1 = "India",
            team2 = "Australia",
            team1Score = "210/5",
            team2Score = "205/8",
            winner = "India",
            wonBy = "5 wickets",
            manOfTheMatch = "Virat Kohli (78*)",
            topScorers = listOf(
                PlayerPerformance("Virat Kohli", 78, 54, 0, 0, 0, false, true),
                PlayerPerformance("Rohit Sharma", 42, 30, 0, 0, 0, false, false),
                PlayerPerformance("David Warner", 65, 42, 0, 0, 0, false, false)
            ),
            bestBowlers = listOf(
                PlayerPerformance("Jasprit Bumrah", 0, 0, 3, 28, 4, false, false),
                PlayerPerformance("Pat Cummins", 0, 0, 2, 30, 4, false, false)
            )
        )
    }
    
    var showDetailedStats by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Match Result") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        
        // Main content
        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            // Match result card
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Trophy icon
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Trophy",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Winning team
                        Text(
                            text = "${matchResult.winner} won by ${matchResult.wonBy}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Match summary
                        Text(
                            text = "${matchResult.team1} vs ${matchResult.team2}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Scores
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(12.dp)
                        ) {
                            // Team 1 score
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = matchResult.team1,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (matchResult.team1 == matchResult.winner) FontWeight.Bold else FontWeight.Normal,
                                    color = if (matchResult.team1 == matchResult.winner) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                                
                                Text(
                                    text = matchResult.team1Score,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (matchResult.team1 == matchResult.winner) FontWeight.Bold else FontWeight.Normal,
                                    color = if (matchResult.team1 == matchResult.winner) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            // Team 2 score
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = matchResult.team2,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (matchResult.team2 == matchResult.winner) FontWeight.Bold else FontWeight.Normal,
                                    color = if (matchResult.team2 == matchResult.winner) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                                
                                Text(
                                    text = matchResult.team2Score,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (matchResult.team2 == matchResult.winner) FontWeight.Bold else FontWeight.Normal,
                                    color = if (matchResult.team2 == matchResult.winner) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Man of the match
                        Text(
                            text = "Player of the Match",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = matchResult.manOfTheMatch,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Toggle detailed stats
            item {
                OutlinedButton(
                    onClick = { showDetailedStats = !showDetailedStats },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(if (showDetailedStats) "Hide Detailed Stats" else "Show Detailed Stats")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (showDetailedStats) Icons.AutoMirrored.Outlined.ArrowForward else Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null
                    )
                }
            }
            
            // Detailed stats
            if (showDetailedStats) {
                // Top scorers
                item {
                    Text(
                        "Top Scorers",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
                
                items(matchResult.topScorers) { player ->
                    PlayerStatItem(player)
                }
                
                // Best bowlers
                item {
                    Text(
                        "Best Bowlers",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
                
                items(matchResult.bestBowlers) { bowler ->
                    PlayerStatItem(bowler, isBowler = true)
                }
            }
            
            // Spacer at the bottom to prevent FAB from covering content
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        // Play again button (FAB)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = onPlayAgain,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Play Again"
                )
            }
        }
    }
}

@Composable
private fun PlayerStatItem(
    player: PlayerPerformance,
    isBowler: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.split(" ").map { it.first() }.joinToString(""),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Player info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (player.isNotOut) FontWeight.Bold else FontWeight.Normal
                )
                
                if (isBowler) {
                    Text(
                        text = "${player.wickets}-${player.runsConceded} (${player.overs} ov)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "${player.runs} (${player.ballsFaced}b)" +
                               if (player.isNotOut) "*" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Strike rate or economy
            if (isBowler) {
                Text(
                    text = "${String.format("%.1f", player.runsConceded.toFloat() / player.overs)} RPO",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "SR: ${String.format("%.1f", player.runs.toFloat() / player.ballsFaced * 100)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class MatchResult(
    val team1: String,
    val team2: String,
    val team1Score: String,
    val team2Score: String,
    val winner: String,
    val wonBy: String,
    val manOfTheMatch: String,
    val topScorers: List<PlayerPerformance>,
    val bestBowlers: List<PlayerPerformance>
)

data class PlayerPerformance(
    val name: String,
    val runs: Int = 0,
    val ballsFaced: Int = 0,
    val wickets: Int = 0,
    val runsConceded: Int = 0,
    val overs: Int = 0,
    val isNotOut: Boolean = false,
    val isManOfTheMatch: Boolean = false
)

@Composable
fun ResultsScreenPreview() {
    MaterialTheme {
        ResultsScreen(
            onPlayAgain = {},
            onBack = {}
        )
    }
}
