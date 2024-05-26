package com.muzafferatmaca.locationtracking.domain.repository

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 16:41
 */
interface LocationRepository {
    suspend fun getCurrentLocation(): LatLng
    fun startLocationUpdates()
    fun stopLocationUpdates()

    val locationUpdates: Flow<LatLng>

}