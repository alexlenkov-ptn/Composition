package presentation.gameFragment

import android.annotation.SuppressLint
import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import data.GameRepositoryImpl
import domain.entity.GameSettings
import domain.entity.Level
import domain.entity.Question
import domain.usecases.GenerateQuestionUseCase
import domain.usecases.GetGameSettingsUseCase
import kotlin.math.min

data class GameFragmentStateUi(
    val gameTimeInSeconds: Int = 0,
    val sum: Int = 0,
    val visibleNumber: Int = 0,
    val countOfRightAnswer: Int = 0,
    val countOfQuestion: Int = 0,
    val percentOfRightAnswer: Int = 0,
    val progressAnswers: String = "",
    val minAnswer: Int = 0,
    val options: List<Int> = listOf(0),
    val timer: String = "",
)

class GameFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    private val repository = GameRepositoryImpl

    private lateinit var gameSettings: GameSettings
    private lateinit var level: Level
    private lateinit var question: Question

    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private val generateGameQuestionUseCase = GenerateQuestionUseCase(repository)

    private val _gameFragmentUiState = MutableLiveData<GameFragmentStateUi>()
    val gameFragmentUiState: LiveData<GameFragmentStateUi>
        get() = _gameFragmentUiState

    private var timer: CountDownTimer? = null

    fun startGame(level: Level) {
        initGameSettings(level)
        generateQuestion()
        initUiState()
        startTimer()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        generateQuestion()
    }

    private fun updateProgress() {
        _gameFragmentUiState.value = gameFragmentUiState.value?.copy(
            percentOfRightAnswer = calculatePercentOfRightAnswers(),
            progressAnswers = String.format(
                context.resources.getString(R.string.progress_answers),
                gameFragmentUiState.value?.countOfRightAnswer.toString(),
                gameSettings.minCountOfRightAnswers.toString()
            )
        )
    }

    private fun calculatePercentOfRightAnswers(): Int {
        with(gameFragmentUiState.value) {
            return ((this?.countOfRightAnswer?.div(this.countOfQuestion.toDouble()))
                ?.times(100))
                ?.toInt()
                ?: 0
        }
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.rightAnswer
        if (number == rightAnswer) {
            _gameFragmentUiState.value = gameFragmentUiState.value?.copy(
                countOfRightAnswer = (gameFragmentUiState.value?.countOfRightAnswer ?: 0) + 1
            )
        }
        _gameFragmentUiState.value = gameFragmentUiState.value?.copy(
            countOfQuestion = (gameFragmentUiState.value?.countOfQuestion ?: 0) + 1
        )
    }

    private fun initGameSettings(level: Level) {
        this.level = level
        gameSettings = getGameSettingsUseCase(level)
    }

    private fun generateQuestion() {
        question = generateGameQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun initUiState() {
        _gameFragmentUiState.value = gameFragmentUiState.value?.copy(
            gameTimeInSeconds = gameSettings.gameTimeInSeconds,
            sum = gameSettings.maxSumValue,
            visibleNumber = question.visibleNumber,
            minAnswer = gameSettings.minCountOfRightAnswers,
            options = question.options,
        )
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            (gameFragmentUiState.value?.gameTimeInSeconds ?: 0) * CONVERT_TO_MS,
            CONVERT_TO_MS,
        ) {
            override fun onTick(millsUntilFinished: Long) {
                _gameFragmentUiState.value = gameFragmentUiState.value?.copy(
                    timer = formatTime(millsUntilFinished)
                )
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    private fun formatTime(millsUntilFinished: Long): String {
        val seconds = millsUntilFinished / CONVERT_TO_MS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun finishGame() {
        TODO()
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        const val CONVERT_TO_MS = 1000L
        const val SECONDS_IN_MINUTES = 60
    }


}