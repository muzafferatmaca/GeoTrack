package com.muzafferatmaca.locationtracking.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.muzafferatmaca.core.baseclass.BaseFragment
import com.muzafferatmaca.core.common.disable
import com.muzafferatmaca.core.common.enable
import com.muzafferatmaca.core.common.selfPermission
import com.muzafferatmaca.core.common.setSafeOnClickListener
import com.muzafferatmaca.core.common.showResultDialog
import com.muzafferatmaca.core.common.showToast
import com.muzafferatmaca.locationtracking.R
import com.muzafferatmaca.locationtracking.databinding.FragmentHomeBinding
import com.muzafferatmaca.locationtracking.service.LocationTrackerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var map: GoogleMap
    @Inject lateinit var userPreferencesViewModel: UserPreferencesViewModel
    @Inject lateinit var locationViewModel: LocationViewModel

    private var locationList = mutableListOf<LatLng>()
    private var lastMarkerLocation: LatLng? = null
    private var polylineList = mutableListOf<Polyline>()
    private var markerList = mutableListOf<Marker>()
    private lateinit var geocoder: Geocoder


    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            locationViewModel.fetchLocation()

        }
    }

    override fun initUi() {
        setTheme()
        observeTheme()
        setUi()
        ifFineLocationPermission()
        getLocation()
        setTracking()
    }

    private fun setUi() {
        saveButtonGettingReady()
        val supportMapFragment = childFragmentManager.findFragmentByTag("mapFragment") as SupportMapFragment?
        supportMapFragment?.getMapAsync {
            map = it
            setMarkerClickListener()
        }
    }

    private fun saveButtonEnabled(){
        binding.saveButton.root.enable()
        binding.saveButton.primaryIconImageView.setImageResource(com.muzafferatmaca.core.R.drawable.ic_location)
        binding.saveButton.primaryTextTextView.text = resources.getString(com.muzafferatmaca.core.R.string.startWalking)
    }

    private fun saveButtonDisabled(){
        binding.saveButton.root.disable()
        binding.saveButton.primaryTextTextView.text = resources.getString(com.muzafferatmaca.core.R.string.stopWalking)
    }

    private fun saveButtonGettingReady(){
        binding.saveButton.root.disable()
        binding.saveButton.primaryIconImageView.setImageResource(com.muzafferatmaca.core.R.drawable.ic_loading)
        binding.saveButton.primaryTextTextView.text = resources.getString(com.muzafferatmaca.core.R.string.gettingReady)
    }

    /**
     * Sets up a click listener for the UI mode button. When the button is clicked,
     * it changes the theme based on the current theme state. The new theme is set in the ViewModel.
     */
    private fun setTheme() {
        binding.uiMode.setSafeOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val currentTheme = userPreferencesViewModel.themeFlow.value
                val newTheme = when (currentTheme) {
                    0, 1 -> 2
                    2 -> 1
                    else -> 0
                }
                userPreferencesViewModel.setTheme(newTheme)
            }
        }
    }

    /**
     * Observes the themeFlow from the ViewModel and updates the UI
     * Accordingly. It sets the appropriate icon for the UI mode button based on the current theme.
     */
    private fun observeTheme() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreferencesViewModel.themeFlow.collect { theme ->
                when (theme) {
                    0 -> binding.uiMode.setImageResource(com.muzafferatmaca.core.R.drawable.ic_mode_light)
                    1 -> binding.uiMode.setImageResource(com.muzafferatmaca.core.R.drawable.ic_mode_night)
                    2 -> binding.uiMode.setImageResource(com.muzafferatmaca.core.R.drawable.ic_mode_light)
                }
            }
        }
    }

    /**
     * When the user launches the application, instead of asking for permission directly,
     * a dialog screen according to why we want the permission
     */
    private fun permissionDialog(descriptionString: String) {
        requireContext().showResultDialog(
            lottieFile = com.muzafferatmaca.core.R.raw.alert,
            isLottie = true,
            titleText = resources.getString(com.muzafferatmaca.core.R.string.permissionDialogTitle),
            titleTextColor = ContextCompat.getColor(requireContext(), com.muzafferatmaca.core.R.color.colorError),
            descriptionText = descriptionString,
            descriptionColor = ContextCompat.getColor(requireContext(), com.muzafferatmaca.core.R.color.colorText),
            yesButtonText = resources.getString(com.muzafferatmaca.core.R.string.allow),
            yesButtonDrawable = ContextCompat.getDrawable(requireContext(), com.muzafferatmaca.core.R.drawable.button_selector_primary_filled)!!,
            noButtonText = resources.getString(com.muzafferatmaca.core.R.string.deny),
            noButtonDrawable = ContextCompat.getDrawable(requireContext(), com.muzafferatmaca.core.R.drawable.button_primary_outline_enabled)!!,
            cancelable = false,
            isOnClickVisibility = true,
            onClickYes = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
            onClickNo = { requireActivity().finish() }
        )
    }

    /**
     * Location permission control and request
     */
    private fun ifFineLocationPermission() {
        requireActivity().selfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            binding.root,
            resources.getString(com.muzafferatmaca.core.R.string.permissionDialogDescription),
            resources.getString(com.muzafferatmaca.core.R.string.allow),
            { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)},
            { permissionDialog(resources.getString(com.muzafferatmaca.core.R.string.permissionDialogDescription)) },
            { locationViewModel.fetchLocation()}
        )
    }

    /**
     * Background location permission control and request
     */
    private fun ifBackgroundLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            requireActivity().selfPermission(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                binding.root,
                resources.getString(com.muzafferatmaca.core.R.string.permissionDialogDescription),
                resources.getString(com.muzafferatmaca.core.R.string.allow),
                { permissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)},
                { permissionDialog(resources.getString(com.muzafferatmaca.core.R.string.backgroundPermissionDialogDescription)) },
                {
                    //TODO background location
                }
            )
        }
    }

    /**
     * Current Location
     */
    private fun getLocation() {
        lifecycleScope.launch {
            locationViewModel.location.collect {latLang ->
                latLang?.let {
                    setLocation(it)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setLocation(latLng: LatLng){

        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(5f)
            .bearing(100f)
            .tilt(45f)
            .build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        map.moveCamera(cameraUpdate)
        map.isMyLocationEnabled = true
        map.uiSettings.apply {
            isCompassEnabled = false
            isMyLocationButtonEnabled = false
        }

        lifecycleScope.launch {
            delay(3000)
            map.animateCamera(CameraUpdateFactory.zoomTo(17f), 2000, null)
            delay(1000)
            saveButtonEnabled()
        }

        setNear(latLng)

    }

    /**
     * To access their own location again when the user scrolls the map
     */
    private fun setNear(userLocation : LatLng){
        binding.nearMeImageView.setSafeOnClickListener {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17f,), 2000, null)
        }
    }

    private fun sendActionService(action : String){
        Intent(requireContext(),LocationTrackerService::class.java).apply {
            this.action = action
            requireContext().startService(this)
        }
    }

    private fun setTracking(){
        binding.saveButton.root.setOnClickListener {
            if (binding.saveButton.root.isEnabled){
                saveButtonDisabled()
                sendActionService(Constants.ACTION_SERVICE_START)
                locationViewModel.startLocationUpdates()
                observeTracker()
            }
        }

        binding.deleteRouteImageView.setSafeOnClickListener {
            sendActionService(Constants.ACTION_SERVICE_STOP)
            locationViewModel.stopLocationUpdates()
            saveButtonEnabled()
            showBiggerPicture()
        }
    }

    private fun observeTracker(){
        lifecycleScope.launch {
            locationViewModel.locationUpdates.collect{
                if (it.longitude != 0.0 && it.longitude != 0.0){
                    locationList.add(it)
                    drawPolyline()
                    followPolyline()
                    addMarkerIfNecessary(it)
                }
            }
        }
    }

    private fun drawPolyline() {
        val polyline = map.addPolyline(
            PolylineOptions().apply {
                width(10f)
                color(Color.BLUE)
                jointType(JointType.ROUND)
                startCap(ButtCap())
                endCap(ButtCap())
                addAll(locationList)
            }
        )
        polylineList.add(polyline)
    }

    private fun followPolyline() {
        if (locationList.isNotEmpty()) {
            val lastLocation = locationList.last()
            val cameraPosition = lastLocation.setCameraPosition()
            val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
            map.animateCamera(cameraUpdate, 1000, null)
        }
    }

    private fun addMarkerIfNecessary(location: LatLng) {
        val lastLocation = lastMarkerLocation
        if (lastLocation == null) {
            val marker = map.addMarker(MarkerOptions().position(location))
            marker?.let { markerList.add(it) }
            lastMarkerLocation = location
        } else {
            val results = FloatArray(1)
            Location.distanceBetween(
                lastLocation.latitude, lastLocation.longitude,
                location.latitude, location.longitude,
                results
            )
            val distance = results[0]
            if (distance >= 100) {
                val marker = map.addMarker(MarkerOptions().position(location))
                marker?.let { markerList.add(it) }
                lastMarkerLocation = location
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setMarkerClickListener() {
        map.setOnMarkerClickListener { marker ->
            val position = marker.position
            showAddress(position)
            true
        }
    }

    private fun showAddress(position: LatLng) {
        try {
            val addresses: List<Address> = geocoder.getFromLocation(position.latitude, position.longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                requireContext().showToast(address)
            } else {
                requireContext().showToast("Address not found")
            }
        } catch (e: IOException) {
            requireContext().showToast("Geocoder service not available")
        }
    }

    private fun showBiggerPicture() {
        val bounds = LatLngBounds.Builder()
        for (location in locationList) {
            bounds.include(location)
        }
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(), 100
            ), 2000, null
        )
    }
}