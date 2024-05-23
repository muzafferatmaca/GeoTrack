package com.muzafferatmaca.locationtracking.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.muzafferatmaca.core.common.PreferencesKeys
import com.muzafferatmaca.locationtracking.domain.entity.UserPreferences
import com.muzafferatmaca.locationtracking.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 14:50
 */
class UserPreferencesRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    UserPreferencesRepository {

    private inline val Preferences.themeKey get() = this[PreferencesKeys.themeKey] ?: 0

    override val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            UserPreferences(theme = preferences.themeKey)
        }.distinctUntilChanged()

    override suspend fun changeTheme(theme: Int) {
        dataStore.edit {pref ->
            pref[PreferencesKeys.themeKey] = theme
        }
    }
}