package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.LevelProgress
import com.example.data.repository.GameRepository
import com.example.model.DifferenceDefinition
import com.example.model.LevelDefinition
import com.example.model.LevelDefinitions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository

    // Observable Level Progress list from Room
    val allProgress: StateFlow<List<LevelProgress>>

    // Gameplay active states
    private val _activeLevel = MutableStateFlow<LevelDefinition>(LevelDefinitions.levels[0])
    val activeLevel: StateFlow<LevelDefinition> = _activeLevel.asStateFlow()

    private val _discoveredIds = MutableStateFlow<Set<Int>>(emptySet())
    val discoveredIds: StateFlow<Set<Int>> = _discoveredIds.asStateFlow()

    // Screen tracking state for backstack
    private val _currentScreen = MutableStateFlow(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Timer tracking
    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()
    private var timerJob: Job? = null

    // Toast/Status messages
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()
    private var messageJob: Job? = null

    // Level completion dialog
    private val _showCompletionDialog = MutableStateFlow(false)
    val showCompletionDialog: StateFlow<Boolean> = _showCompletionDialog.asStateFlow()

    // Success effect trigger
    private val _isLevelFailed = MutableStateFlow(false)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.levelProgressDao())

        allProgress = repository.allProgress
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Prep the progress table on startup with levels 1..20
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    enum class Screen {
        Home,
        LevelSelector,
        GamePlay,
        Info
    }

    private val backStack = mutableListOf<Screen>(Screen.Home)

    fun navigateTo(screen: Screen) {
        if (screen == Screen.Home) {
            backStack.clear()
            backStack.add(Screen.Home)
        } else {
            if (backStack.isEmpty() || backStack.last() != screen) {
                backStack.add(screen)
            }
        }
        _currentScreen.value = screen
        if (screen == Screen.GamePlay) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    fun goBack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
            val prevScreen = backStack.lastOrNull() ?: Screen.Home
            _currentScreen.value = prevScreen
            if (prevScreen == Screen.GamePlay) {
                startTimer()
            } else {
                stopTimer()
            }
            return true
        }
        return false
    }

    fun selectLevel(levelId: Int) {
        val lvl = LevelDefinitions.levels.firstOrNull { it.id == levelId } ?: LevelDefinitions.levels[0]
        _activeLevel.value = lvl
        _discoveredIds.value = emptySet()
        _showCompletionDialog.value = false
        _timerSeconds.value = 0
        navigateTo(Screen.GamePlay)
    }

    private fun startTimer() {
        timerJob?.cancel()
        _timerSeconds.value = 0
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _timerSeconds.value += 1
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Tapped coordinates checks against known difference hitboxes
     */
    fun handleTapCoordinates(xRatio: Float, yRatio: Float) {
        if (_showCompletionDialog.value) return

        val currentLevelData = _activeLevel.value
        val alreadyFound = _discoveredIds.value

        // Check if tap fell inside of any of the 4 difference points tolerance radius
        val tappedDiff = currentLevelData.differences.firstOrNull { diff ->
            if (alreadyFound.contains(diff.id)) return@firstOrNull false

            // Quadratic distance formula
            val dx = xRatio - diff.x
            // Adjust ratio aspect since images are 1.5 ratio width to height
            val dy = (yRatio - diff.y) / 1.5f
            val distance = kotlin.math.sqrt(dx * dx + dy * dy)

            distance <= diff.radius
        }

        if (tappedDiff != null) {
            // Spotted! Add to set
            val updated = alreadyFound + tappedDiff.id
            _discoveredIds.value = updated

            val remaining = currentLevelData.differences.size - updated.size
            if (remaining > 0) {
                triggerStatus("成功找出一处不同！还剩 $remaining 处")
            } else {
                // Found all 4 difference points! Success Level Complete
                stopTimer()
                triggerStatus("大功告成！成功破解《${currentLevelData.idiom}》")
                _showCompletionDialog.value = true

                viewModelScope.launch {
                    // Save best progress to Room
                    repository.completeLevel(
                        levelId = currentLevelData.id,
                        stars = 4, // Found all 4
                        timeSeconds = _timerSeconds.value
                    )
                }
            }
        } else {
            // Visual error or minor penalty can go here, let's keep it clean
            triggerStatus("那里看起来没有不同，再仔细瞅瞅。")
        }
    }

    fun nextLevel() {
        val nextId = _activeLevel.value.id + 1
        val hasNext = LevelDefinitions.levels.any { it.id == nextId }
        if (hasNext) {
            selectLevel(nextId)
        } else {
            navigateTo(Screen.LevelSelector)
        }
    }

    fun resetGameProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            _discoveredIds.value = emptySet()
            _showCompletionDialog.value = false
            triggerStatus("所有关卡解锁进度已重新初始化")
        }
    }

    fun triggerStatus(message: String) {
        messageJob?.cancel()
        _statusMessage.value = message
        messageJob = viewModelScope.launch {
            delay(2000)
            _statusMessage.value = null
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
