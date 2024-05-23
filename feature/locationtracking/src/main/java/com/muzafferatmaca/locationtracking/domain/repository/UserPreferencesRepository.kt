package com.muzafferatmaca.locationtracking.domain.repository

import com.muzafferatmaca.locationtracking.domain.entity.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 14:51
 */
interface UserPreferencesRepository {

    val userPreferences: Flow<UserPreferences>

    suspend fun changeTheme(theme: Int)
}