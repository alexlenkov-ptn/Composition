package domain.usecases

import domain.entity.Question
import domain.repository.GameRepository

class GenerateQuestionUseCase(private val repository: GameRepository) {
    operator fun invoke(maxSumValue: Int): Question =
        repository.generateQuestion(COUNT_OF_OPTIONS, maxSumValue)

    private companion object {
        private const val COUNT_OF_OPTIONS = 6
    }
}