package io.awais.cricket_championship.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.awais.cricket_championship.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedUserTeam by viewModel.selectedUserTeam.collectAsState()
    val selectedOpponentTeam by viewModel.selectedOpponentTeam.collectAsState()
    val tossResult by viewModel.tossResult.collectAsState()
    val userWonToss by viewModel.userWonToss.collectAsState()

    Scaffold(
        topBar = {
            if (uiState.screen != GameScreenState.Screen.HOME) {
                TopAppBar(
                    title = { Text(getScreenTitle(uiState.screen)) }, navigationIcon = {
                    if (uiState.screen != GameScreenState.Screen.TEAM_SELECTION) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack, contentDescription = "Back"
                            )
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                )
            }
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Show loading indicator if needed
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center), color = MaterialTheme.colorScheme.primary
                )
            }

            // Show error message if any
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            // Show appropriate screen based on game state
            AnimatedContent(
                targetState = uiState.screen, transitionSpec = {
                    (slideInHorizontally { height -> height } + fadeIn()).togetherWith(slideOutHorizontally { height -> -height } + fadeOut())
                }, label = "screen_transition"
            ) { targetState ->
                when (targetState) {
                    GameScreenState.Screen.HOME -> HomeScreen(
                        onNewGame = { viewModel.startNewGame() })

                    GameScreenState.Screen.TEAM_SELECTION -> TeamSelectionScreen(onTeamsSelected = { userTeam, opponentTeam ->
                        viewModel.selectTeams(userTeam, opponentTeam)
                    }, onBack = { viewModel.navigateBack() })

                    GameScreenState.Screen.TOSS -> TossScreen(
                        userTeam = selectedUserTeam ?: "Your Team",
                        opponentTeam = selectedOpponentTeam ?: "Opponent Team",
                        onTossCompleted = { userWon ->
                            viewModel.onTossCompleted(userWon)
                        },
                        onBack = { viewModel.navigateBack() })

                    GameScreenState.Screen.MATCH -> MatchScreen(
                        userTeam = selectedUserTeam ?: "Your Team",
                        opponentTeam = selectedOpponentTeam ?: "Opponent Team",
                        onMatchComplete = { viewModel.onMatchComplete() },
                        onBack = { viewModel.navigateBack() })

                    GameScreenState.Screen.RESULTS -> ResultsScreen(
                        onPlayAgain = { viewModel.resetGame() },
                        onBack = { viewModel.navigateBack() })
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    onNewGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Cricket Championship",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onNewGame, modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
        ) {
            Text("New Game")
        }
    }
}

@Composable
private fun MatchScreen(
    userTeam: String, opponentTeam: String, onMatchComplete: () -> Unit, onBack: () -> Unit
) {
    MatchScreen(
        onMatchComplete = onMatchComplete, onBack = onBack
    )
}

private fun getScreenTitle(screen: GameScreenState.Screen): String {
    return when (screen) {
        GameScreenState.Screen.HOME -> "Cricket Championship"
        GameScreenState.Screen.TEAM_SELECTION -> "Select Teams"
        GameScreenState.Screen.TOSS -> "Toss"
        GameScreenState.Screen.MATCH -> "Match"
        GameScreenState.Screen.RESULTS -> "Match Results"
    }
}

sealed class GameScreenState {
    data class State(
        val screen: Screen = Screen.HOME, val isLoading: Boolean = false, val error: String? = null
    )

    enum class Screen {
        HOME, TEAM_SELECTION, TOSS, MATCH, RESULTS
    }
}
