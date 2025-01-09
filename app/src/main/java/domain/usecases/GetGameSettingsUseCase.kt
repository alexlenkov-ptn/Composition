package domain.usecases

import domain.entity.GameSettings
import domain.entity.Level
import domain.repository.GameRepository

class GetGameSettingsUseCase(private val repository: GameRepository) {
    operator fun invoke(level: Level): GameSettings = repository.getGameSettings(level)
}