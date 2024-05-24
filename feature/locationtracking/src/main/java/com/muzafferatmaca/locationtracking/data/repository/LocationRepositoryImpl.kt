package com.muzafferatmaca.locationtracking.data.repository

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.muzafferatmaca.locationtracking.domain.repository.LocationRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 16:43
 */
class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Pair<Double, Double> {
        val location: Location? = fusedLocationProviderClient.lastLocation.await()
        return location?.let {
            Pair(it.latitude, it.longitude)
        } ?: Pair(0.0, 0.0)
    }
}