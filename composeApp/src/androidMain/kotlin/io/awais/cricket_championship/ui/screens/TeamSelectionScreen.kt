package io.awais.cricket_championship.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.awais.cricket_championship.R

@Composable
fun TeamSelectionScreen(
    onTeamsSelected: (String, String) -> Unit,
    onBack: () -> Unit,
    availableTeams: List<String> = listOf(
        "Australia",
        "England",
        "India",
        "New Zealand",
        "Pakistan",
        "South Africa",
        "Sri Lanka",
        "West Indies"
    )
) {
    var selectedUserTeam by remember { mutableStateOf<String?>(null) }
    var selectedOpponentTeam by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button at the top
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
                text = "Select Teams",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Select Your Team",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // User Team Selection
        TeamSelector(
            teams = availableTeams,
            selectedTeam = selectedUserTeam,
            onTeamSelected = { team ->
                selectedUserTeam = team
                // Reset opponent team if it was the same as the newly selected user team
                if (selectedOpponentTeam == team) {
                    selectedOpponentTeam = null
                }
            },
            label = "Your Team"
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Opponent Team Selection
        TeamSelector(
            teams = availableTeams.filter { it != selectedUserTeam },
            selectedTeam = selectedOpponentTeam,
            onTeamSelected = { team ->
                selectedOpponentTeam = team
            },
            label = "Opponent Team",
            enabled = selectedUserTeam != null
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Continue Button
        Button(
            onClick = {
                val userTeam = selectedUserTeam ?: return@Button
                val opponentTeam = selectedOpponentTeam ?: return@Button
                onTeamsSelected(userTeam, opponentTeam)
            },
            enabled = selectedUserTeam != null && selectedOpponentTeam != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Continue to Toss")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamSelector(
    teams: List<String>,
    selectedTeam: String?,
    onTeamSelected: (String) -> Unit,
    label: String,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded && enabled,
            onExpandedChange = { if (enabled) expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedTeam ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                placeholder = { 
                    Text(
                        if (enabled) "Select $label" else "Select your team first",
                        color = if (enabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.5f)
                    ) 
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    disabledTextColor = LocalContentColor.current.copy(alpha = 0.38f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            if (enabled) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (teams.isEmpty()) {
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "No teams available", 
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            onClick = { expanded = false }
                        )
                    } else {
                        teams.forEach { team ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        team,
                                        style = MaterialTheme.typography.bodyLarge
                                    ) 
                                },
                                onClick = {
                                    onTeamSelected(team)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
