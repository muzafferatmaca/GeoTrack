package com.muzafferatmaca.locationtracking.domain.usecase.location

import com.muzafferatmaca.locationtracking.domain.repository.LocationRepository
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 16:49
 */
class GetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Pair<Double, Double> {
        return locationRepository.getCurrentLocation()
    }
}