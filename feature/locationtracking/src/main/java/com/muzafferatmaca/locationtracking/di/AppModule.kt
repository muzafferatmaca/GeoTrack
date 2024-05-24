package com.muzafferatmaca.locationtracking.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.muzafferatmaca.core.common.datastore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 14:48
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideDataStore(context: Context): DataStore<Preferences> = context.datastore

    @Provides
    @Singleton
    fun provideFusedLocation(context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}