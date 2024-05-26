package com.muzafferatmaca.locationtracking.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
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
import com.google.maps.android.SphericalUtil
import com.muzafferatmaca.core.baseclass.BaseFragment
import com.muzafferatmaca.core.common.invisible
import com.muzafferatmaca.core.common.selfPermission
import com.muzafferatmaca.core.common.setSafeOnClickListener
import com.muzafferatmaca.core.common.showResultDialog
import com.muzafferatmaca.core.common.visible
import com.muzafferatmaca.locationtracking.databinding.FragmentHomeBinding
import com.muzafferatmaca.locationtracking.service.LocationTrackerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
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

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            locationViewModel.fetchLocation()

        }
    }

    private val notificationPermissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){}


    override fun initUi() {
        setTheme()
        observeTheme()
        setUi()
        ifFineLocationPermission()
        setTracking()
        removeTrackerClick()
    }

    private fun setUi() {
        val supportMapFragment = childFragmentManager.findFragmentByTag("mapFragment") as SupportMapFragment?
        supportMapFragment?.getMapAsync {
            map = it
            setMarkerClickListener()
            getLocation()
        }
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
            noButtonText = resources.getString(com.muzafferatmaca.core.R.string.closeApp),
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

                }
            )
        }
    }

    /**
     * Notification permission control and request
     */
    private fun ifNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requireActivity().selfPermission(
                Manifest.permission.POST_NOTIFICATIONS,
                binding.root,
                resources.getString(com.muzafferatmaca.core.R.string.permissionDialogDescription),
                resources.getString(com.muzafferatmaca.core.R.string.allow),
                { notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)},
                {  notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)},
                {}
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
                    setNear(latLang)
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
        }
        observeTrackerService()
    }

    /**
     * To access their own location again when the user scrolls the map
     */
    private fun setNear(userLocation : LatLng){
        binding.nearMeImageView.setOnClickListener {
            if (locationList.isNotEmpty()) {
                val lastLocation = locationList.last()
                val cameraPosition = lastLocation.setCameraPosition()
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                map.animateCamera(cameraUpdate, 1000, null)
            }else{
                locationViewModel.fetchLocation()
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17f,), 2000, null)
            }
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
            ifNotificationPermission()
            ifBackgroundLocationPermission()
            removeTracker()
            sendActionService(Constants.ACTION_SERVICE_START)
            binding.saveButton.root.invisible()
            binding.stopButton.root.visible()
        }
        binding.stopButton.root.setOnClickListener {
            showBiggerPicture()
            sendActionService(Constants.ACTION_SERVICE_STOP)
            binding.saveButton.root.visible()
            binding.stopButton.root.invisible()
        }
    }

    private fun removeTrackerClick(){
        binding.deleteRouteImageView.setSafeOnClickListener {
           removeTracker()
        }
    }

    private fun removeTracker(){
        for (polyline in polylineList){
            polyline.remove()
        }
        for (marker in markerList){
            marker.remove()
        }
        locationList.clear()
    }

    private fun observeTrackerService() {
        LocationTrackerService.locationList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                locationList = it
                if (locationList.size > 1) {
                    binding.stopButton.root.visible()
                    binding.saveButton.root.invisible()
                }
                drawPolyline()
                followPolyline()
                addMarkerIfNecessary(it)
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

    /**
     * Add 1 marker every 100 meters
     */
    private fun addMarkerIfNecessary(locations: MutableList<LatLng>) {
        val startIndex = if (lastMarkerLocation != null) locations.indexOf(lastMarkerLocation) + 1 else 0
        for (i in startIndex until locations.size) {
            if (lastMarkerLocation != null && i > 0) {
                val distance = SphericalUtil.computeDistanceBetween(lastMarkerLocation, locations[i])
                if (distance < 100) continue
            }
            val marker = map.addMarker(MarkerOptions().position(locations[i]))
            marker?.let { markerList.add(it) }
            lastMarkerLocation = locations[i]
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setMarkerClickListener() {
        map.setOnMarkerClickListener { marker ->
            val position = marker.position
            showAddress(marker, position)
            true
        }
    }

    private fun showAddress(marker: Marker, position: LatLng) {

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(position.latitude, position.longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                marker.title = address
                marker.showInfoWindow()
            } else {
                marker.title = "Address not found"
                marker.showInfoWindow()
            }
        } catch (e: IOException) {
            marker.title = "Geocoder service not available"
            marker.showInfoWindow()
        }
    }

    private fun showBiggerPicture() {
        val boundsBuilder = LatLngBounds.Builder()
        var hasValidPoints = false

        if (locationList.isNotEmpty()) {
            for (location in locationList) {
                if (location.longitude != 0.0 && location.latitude != 0.0) {
                    boundsBuilder.include(location)
                    hasValidPoints = true
                }
            }
        }

        if (hasValidPoints) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(), 100
                ), 2000, null
            )
        }

    }



}