package io.awais.cricket_championship.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.awais.cricket_championship.engine.MatchEngine
import io.awais.cricket_championship.ui.screens.GameScreenState
import io.awais.cricket_championship.ui.screens.PlayerPerformance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import io.awais.cricket_championship.engine.result.MatchResult

@HiltViewModel
class GameViewModel @Inject constructor(
    private val matchEngine: MatchEngine
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameScreenState.State())
    val uiState: StateFlow<GameScreenState.State> = _uiState.asStateFlow()

    private val _selectedUserTeam = MutableStateFlow<String?>(null)
    val selectedUserTeam: StateFlow<String?> = _selectedUserTeam.asStateFlow()

    private val _selectedOpponentTeam = MutableStateFlow<String?>(null)
    val selectedOpponentTeam: StateFlow<String?> = _selectedOpponentTeam.asStateFlow()

    private val _tossResult = MutableStateFlow<String?>(null)
    val tossResult: StateFlow<String?> = _tossResult.asStateFlow()

    private val _userWonToss = MutableStateFlow(false)
    val userWonToss: StateFlow<Boolean> = _userWonToss.asStateFlow()

    private val screenHistory = ArrayDeque<GameScreenState.Screen>()

    init {
        // Initialize with home screen
        navigateTo(GameScreenState.Screen.HOME)
    }

    fun startNewGame() {
        navigateTo(GameScreenState.Screen.TEAM_SELECTION)
    }

    fun selectTeams(userTeam: String, opponentTeam: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _selectedUserTeam.value = userTeam
                _selectedOpponentTeam.value = opponentTeam
                navigateTo(GameScreenState.Screen.TOSS)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to select teams: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onTossCompleted(userWon: Boolean) {
        _userWonToss.value = userWon
        navigateTo(GameScreenState.Screen.MATCH)
    }

    fun onMatchComplete() {
        // In a real app, this would come from the match engine
//        val result = MatchResult(
//            battingFirstTeam = _uiState.value.selectedUserTeam ?: "Team A",
//            fieldingFirstTeam = _uiState.value.selectedOpponentTeam ?: "Team B",
//            team1Score = "210/5 (20.0)",
//            team2Score = "205/8 (20.0)",
//            winner = _uiState.value.selectedUserTeam ?: "Team A",
//            wonBy = "5 wickets",
//            manOfTheMatch = "Player of the Match",
//            topScorers = listOf(
//                PlayerPerformance("Batsman 1", 78, 54, 0, 0, 0, false, true),
//                PlayerPerformance("Batsman 2", 42, 30, 0, 0, 0, false, false)
//            ),
//            bestBowlers = listOf(
//                PlayerPerformance("Bowler 1", 0, 0, 3, 28, 4, false, false)
//            )
//        )
        
//        _uiState.update { currentState ->
//            currentState.copy(
//                currentScreen = GameScreenState.Screen.RESULTS,
//                screenHistory = currentState.screenHistory + currentState.currentScreen,
//                matchResult = result
//            )
//        }
    }

    fun resetGame() {
        _selectedUserTeam.value = null
        _selectedOpponentTeam.value = null
        _tossResult.value = null
        _userWonToss.value = false
        navigateTo(GameScreenState.Screen.HOME, clearHistory = true)
    }

    fun navigateBack() {
        if (screenHistory.size > 1) {
            screenHistory.removeLast()
            val previousScreen = screenHistory.last()
            _uiState.update { it.copy(screen = previousScreen) }
        }
    }

    private fun navigateTo(screen: GameScreenState.Screen, clearHistory: Boolean = false) {
        if (clearHistory) {
            screenHistory.clear()
        }
        screenHistory.addLast(screen)
        _uiState.update { it.copy(screen = screen, isLoading = false, error = null) }
    }
}
