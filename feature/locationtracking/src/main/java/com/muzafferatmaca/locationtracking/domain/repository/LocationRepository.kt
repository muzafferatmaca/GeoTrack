package com.muzafferatmaca.locationtracking.domain.repository

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 16:41
 */
interface LocationRepository {
    suspend fun getCurrentLocation(): Pair<Double, Double>
}