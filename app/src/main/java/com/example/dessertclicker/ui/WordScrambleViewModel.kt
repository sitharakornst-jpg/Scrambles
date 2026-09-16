package com.example.dessertclicker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dessertclicker.data.WordItem
import com.example.dessertclicker.data.WordsData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val currentScrambledWord: String = "",
    val currentWordItem: WordItem? = null,
    val isShowingPreview: Boolean = false,
    val currentWordCount: Int = 0,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false,
    val timeLeft: Int = 60,
    val guessCount: Int = 0,
    val message: String? = null
)

class WordScrambleViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var currentItem: WordItem? = null
    private var usedWords: MutableSet<String> = mutableSetOf()
    
    private var timerJob: Job? = null

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(score = 0, currentWordCount = 0)
        getNextWord()
    }

    private fun getNextWord() {
        if (usedWords.size == WordsData.dailyVocabulary.size) {
            _uiState.update { it.copy(isGameOver = true) }
            timerJob?.cancel()
        } else {
            val nextItem = WordsData.dailyVocabulary.filter { it.word !in usedWords }.random()
            currentItem = nextItem
            usedWords.add(nextItem.word)
            
            _uiState.update {
                it.copy(
                    currentWordItem = nextItem,
                    isShowingPreview = true,
                    currentWordCount = it.currentWordCount + 1,
                    isGuessedWordWrong = false,
                    timeLeft = 60,
                    guessCount = 0,
                    message = null
                )
            }
            timerJob?.cancel()
        }
    }

    fun startScramble() {
        currentItem?.let { item ->
            _uiState.update {
                it.copy(
                    isShowingPreview = false,
                    currentScrambledWord = scrambleWord(item.word)
                )
            }
            startTimer()
        }
    }

    private fun scrambleWord(word: String): String {
        val chars = word.uppercase().toCharArray()
        chars.shuffle()
        while (String(chars) == word.uppercase() && word.length > 1) {
            chars.shuffle()
        }
        return String(chars)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1000L)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            _uiState.update { it.copy(message = "Time's up! It was ${currentItem?.word}") }
            delay(1500)
            getNextWord()
        }
    }

    fun checkUserGuess(guess: String) {
        if (guess.equals(currentItem?.word, ignoreCase = true)) {
            val updatedScore = _uiState.value.score + 10
            _uiState.update { it.copy(score = updatedScore, message = "Correct! ${currentItem?.word} = ${currentItem?.definition}") }
            viewModelScope.launch {
                delay(1500)
                getNextWord()
            }
        } else {
            val newGuessCount = _uiState.value.guessCount + 1
            if (newGuessCount >= 5) {
                _uiState.update { 
                    it.copy(
                        guessCount = newGuessCount, 
                        isGuessedWordWrong = true,
                        message = "Out of guesses! It was ${currentItem?.word}"
                    ) 
                }
                viewModelScope.launch {
                    delay(1500)
                    getNextWord()
                }
            } else {
                _uiState.update { it.copy(isGuessedWordWrong = true, guessCount = newGuessCount) }
            }
        }
    }

    fun skipWord() {
        _uiState.update { it.copy(message = "Skipped! It was ${currentItem?.word}") }
        viewModelScope.launch {
            delay(1000)
            getNextWord()
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
