package io.awais.cricket_championship.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TossScreen(
    userTeam: String,
    opponentTeam: String,
    onTossCompleted: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var tossCall by remember { mutableStateOf<String?>(null) }
    var tossResult by remember { mutableStateOf<String?>(null) }
    var isTossing by remember { mutableStateOf(false) }
    var tossCompleted by remember { mutableStateOf(false) }
    var userWonToss by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button at the top
        if (!tossCompleted) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Toss",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (!tossCompleted) {
            Text(
                text = "Toss Time!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "$userTeam vs $opponentTeam",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Call heads or tails to decide who bats first",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
        
        if (!tossCompleted) {
            // Toss call buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                TossButton(
                    text = "Heads",
                    selected = tossCall == "heads",
                    onClick = { tossCall = "heads" },
                    enabled = !isTossing
                )
                
                TossButton(
                    text = "Tails",
                    selected = tossCall == "tails",
                    onClick = { tossCall = "tails" },
                    enabled = !isTossing
                )
            }
            
            // Toss button
            Button(
                onClick = {
                    if (tossCall != null) {
                        isTossing = true
                        // Simulate toss animation
//                        LaunchedEffect(Unit) {
//                            // Animate the coin flip
//                            delay(1500) // Simulate toss animation
//
//                            // Determine result
//                            val result = listOf("heads", "tails").random()
//                            tossResult = result
//                            userWonToss = tossCall == result
//
//                            // Show result after a short delay
//                            delay(500)
//                            tossCompleted = true
//                            isTossing = false
//
//                            // Show result with a bit of delay for better UX
//                            delay(300)
//                            showResult = true
//                        }
                    }
                },
                enabled = tossCall != null && !isTossing,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                if (isTossing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Toss the Coin!")
                }
            }
            
            // Coin toss animation
            if (isTossing) {
                Spacer(modifier = Modifier.height(32.dp))
                AnimatedVisibility(
                    visible = isTossing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    CoinTossAnimation()
                }
            }
        } else {
            // Toss result with animation
            AnimatedVisibility(
                visible = showResult,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    // Coin with result
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            text = tossResult?.replaceFirstChar { it.uppercase() } ?: "",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Result text
                    Text(
                        text = if (userWonToss) "You won the toss!" else "You lost the toss!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (userWonToss) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "It's $tossResult!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }
            
            // Action buttons with animation
            AnimatedVisibility(
                visible = showResult,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Button(
                        onClick = { onTossCompleted(userWonToss) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Continue to Match")
                    }
                    
                    OutlinedButton(
                        onClick = {
                            showResult = false
                            tossCall = null
                            tossResult = null
                            tossCompleted = false
                            userWonToss = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry toss",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry Toss")
                    }
                }
            }
        }
    }
}

@Composable
private fun TossButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        ),
        enabled = enabled,
        modifier = Modifier.size(100.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CoinTossAnimation() {
    var rotation by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            rotation += 45f
            kotlinx.coroutines.delay(100)
        }
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .rotate(rotation)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 8.dp,
            modifier = Modifier.size(80.dp)
        ) {
            // Coin design
        }
    }
}
