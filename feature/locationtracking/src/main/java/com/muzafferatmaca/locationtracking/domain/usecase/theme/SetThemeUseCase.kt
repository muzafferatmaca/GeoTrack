package com.muzafferatmaca.locationtracking.domain.usecase.theme

import com.muzafferatmaca.locationtracking.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 14:56
 */
class SetThemeUseCase @Inject constructor(private val userPreferencesRepository: UserPreferencesRepository) {

    suspend operator fun invoke(theme: Int) = userPreferencesRepository.changeTheme(theme)

}