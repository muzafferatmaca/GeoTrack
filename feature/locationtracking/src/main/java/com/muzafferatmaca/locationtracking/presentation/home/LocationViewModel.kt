package com.muzafferatmaca.locationtracking.presentation.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.muzafferatmaca.core.baseclass.BaseViewModel
import com.muzafferatmaca.locationtracking.domain.usecase.location.GetLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 16:49
 */
class LocationViewModel @Inject constructor(
    application: Application,
    private val getLocationUseCase: GetLocationUseCase
) : BaseViewModel(application) {

    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location: StateFlow<Pair<Double, Double>?> get() = _location

    fun fetchLocation() {
        viewModelScope.launch {
            val currentLocation = getLocationUseCase()
            _location.value = currentLocation
        }
    }
}