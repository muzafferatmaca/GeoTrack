package com.muzafferatmaca.locationtracking.domain.usecase.theme

import com.muzafferatmaca.locationtracking.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 14:55
 */
class GetThemeUseCase @Inject constructor(private val userPreferencesRepository: UserPreferencesRepository) {

    operator fun invoke(): Flow<Int> {
        return userPreferencesRepository.userPreferences
            .map { it.theme }
    }
}