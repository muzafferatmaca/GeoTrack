package com.muzafferatmaca.locationtracking.presentation.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.muzafferatmaca.core.baseclass.BaseViewModel
import com.muzafferatmaca.locationtracking.domain.usecase.location.GetLocationUseCase
import com.muzafferatmaca.locationtracking.domain.usecase.location.LocationUpdateUseCase
import com.muzafferatmaca.locationtracking.domain.usecase.location.StartLocationUpdatesUseCase
import com.muzafferatmaca.locationtracking.domain.usecase.location.StopLocationUpdatesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 16:49
 */
class LocationViewModel @Inject constructor(
    application: Application,
    private val getLocationUseCase: GetLocationUseCase,
    private val startLocationUpdatesUseCase: StartLocationUpdatesUseCase,
    private val stopLocationUpdatesUseCase: StopLocationUpdatesUseCase,
    locationUpdateUseCase: LocationUpdateUseCase
) : BaseViewModel(application) {

    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> get() = _location

    val locationUpdates: Flow<LatLng>  = locationUpdateUseCase.invoke()

    fun fetchLocation() {
        viewModelScope.launch {
            val currentLocation = getLocationUseCase()
            _location.value = currentLocation
        }
    }

    fun startLocationUpdates() {
        startLocationUpdatesUseCase.invoke()

    }

    fun stopLocationUpdates() {
        stopLocationUpdatesUseCase.invoke()
    }
}