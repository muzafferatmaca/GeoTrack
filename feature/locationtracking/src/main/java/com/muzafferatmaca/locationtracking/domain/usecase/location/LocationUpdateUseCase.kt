package com.muzafferatmaca.locationtracking.domain.usecase.location

import com.muzafferatmaca.locationtracking.domain.repository.LocationRepository
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 25.05.2024 at 22:10
 */
class LocationUpdateUseCase @Inject constructor(private val locationRepository: LocationRepository) {

    operator fun invoke() = locationRepository.locationUpdates
}