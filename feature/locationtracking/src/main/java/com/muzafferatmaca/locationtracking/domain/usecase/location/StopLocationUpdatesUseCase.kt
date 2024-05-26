package com.muzafferatmaca.locationtracking.domain.usecase.location

import com.muzafferatmaca.locationtracking.domain.repository.LocationRepository
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 25.05.2024 at 21:02
 */
class StopLocationUpdatesUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke()  = locationRepository.stopLocationUpdates()
}