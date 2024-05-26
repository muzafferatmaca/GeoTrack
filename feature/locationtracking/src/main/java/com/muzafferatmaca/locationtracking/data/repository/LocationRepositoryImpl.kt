package com.muzafferatmaca.locationtracking.data.repository

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.muzafferatmaca.locationtracking.domain.repository.LocationRepository
import com.muzafferatmaca.locationtracking.presentation.home.Constants.LOCATION_FASTEST_UPDATE_INTERVAL
import com.muzafferatmaca.locationtracking.presentation.home.Constants.LOCATION_UPDATE_INTERVAL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 16:43
 */
class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationRepository {

    private val _locationFlow = MutableStateFlow(LatLng(0.0,0.0))
    override val locationUpdates: StateFlow<LatLng> = _locationFlow

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.lastOrNull()?.let { location ->
                _locationFlow.tryEmit(LatLng(location.latitude, location.longitude))
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LatLng {
        val location: Location? = fusedLocationProviderClient.lastLocation.await()
        return location?.let {
            LatLng(it.latitude, it.longitude)
        } ?: LatLng(0.0, 0.0)
    }

    @SuppressLint("MissingPermission")
    override fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        )
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(LOCATION_FASTEST_UPDATE_INTERVAL)
            .build()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


}