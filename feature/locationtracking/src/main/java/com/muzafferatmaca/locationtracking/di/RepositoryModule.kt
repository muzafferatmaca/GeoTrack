package com.muzafferatmaca.locationtracking.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.muzafferatmaca.locationtracking.data.repository.LocationRepositoryImpl
import com.muzafferatmaca.locationtracking.data.repository.UserPreferencesRepositoryImpl
import com.muzafferatmaca.locationtracking.domain.repository.LocationRepository
import com.muzafferatmaca.locationtracking.domain.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 14:48
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): UserPreferencesRepository =
        UserPreferencesRepositoryImpl(dataStore)

    @Provides
    @Singleton
    fun provideLocationRepository(fusedLocationProviderClient: FusedLocationProviderClient): LocationRepository =
        LocationRepositoryImpl(fusedLocationProviderClient)

}