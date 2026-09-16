package com.example.dessertclicker.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dessertclicker.R

// Daily Vocabulary Theme Colors
private val VocabPrimary = Color(0xFF2E7D32) // Forest Green
private val VocabSecondary = Color(0xFFE8F5E9) // Light Green
private val VocabAccent = Color(0xFF1565C0) // Deep Blue
private val VocabBackground = Color(0xFFF5F5F5)

@Composable
fun WordScrambleScreen(
    modifier: Modifier = Modifier,
    viewModel: WordScrambleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var userGuess by remember { mutableStateOf("") }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
            userGuess = ""
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VocabBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "DAILY VOCABULARY",
                style = MaterialTheme.typography.headlineLarge,
                color = VocabPrimary,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = uiState.isShowingPreview,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                WordPreviewCard(
                    word = uiState.currentWordItem?.word ?: "",
                    definition = uiState.currentWordItem?.definition ?: "",
                    onStart = { viewModel.startScramble() }
                )
            }

            AnimatedVisibility(
                visible = !uiState.isShowingPreview,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                GamePlayCard(
                    uiState = uiState,
                    userGuess = userGuess,
                    onUserGuessChange = { userGuess = it },
                    onSubmit = { viewModel.checkUserGuess(userGuess) },
                    onSkip = { 
                        viewModel.skipWord()
                        userGuess = ""
                    }
                )
            }
        }

        if (uiState.isGameOver) {
            AlertDialog(
                onDismissRequest = { viewModel.resetGame() },
                containerColor = Color.White,
                titleContentColor = VocabPrimary,
                textContentColor = Color.Black,
                title = { Text("COMPLETED!") },
                text = { Text("Great job learning! Your Final Score: ${uiState.score}") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.resetGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = VocabPrimary)
                    ) {
                        Text("REPLAY")
                    }
                }
            )
        }
    }
}

@Composable
fun WordPreviewCard(
    word: String,
    definition: String,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LEARN THIS WORD",
                style = MaterialTheme.typography.titleSmall,
                color = VocabAccent
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = word,
                style = MaterialTheme.typography.displayMedium,
                color = VocabPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Meaning: $definition",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = VocabPrimary)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("READY TO SCRAMBLE")
            }
        }
    }
}

@Composable
fun GamePlayCard(
    uiState: GameUiState,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "WORD ${uiState.currentWordCount}/20",
                    style = MaterialTheme.typography.labelLarge,
                    color = VocabAccent
                )
                Text(
                    text = "SCORE: ${uiState.score}",
                    style = MaterialTheme.typography.labelLarge,
                    color = VocabPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = uiState.currentScrambledWord,
                style = MaterialTheme.typography.displaySmall,
                color = Color.Black,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Hint: show definition
            Text(
                text = "Hint: ${uiState.currentWordItem?.definition}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = if (uiState.timeLeft < 10) Color.Red else VocabAccent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "TIME: ${uiState.timeLeft}s",
                    color = if (uiState.timeLeft < 10) Color.Red else Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "GUESS: ${uiState.guessCount}/5",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = userGuess,
                onValueChange = onUserGuessChange,
                label = { Text("Unscramble the word") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.isGuessedWordWrong,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VocabPrimary)
                ) {
                    Text("SKIP")
                }
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = VocabPrimary)
                ) {
                    Text("SUBMIT")
                }
            }
        }
    }
}
