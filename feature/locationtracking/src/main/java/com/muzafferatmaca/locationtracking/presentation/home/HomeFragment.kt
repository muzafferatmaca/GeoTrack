package com.muzafferatmaca.locationtracking.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.muzafferatmaca.core.baseclass.BaseFragment
import com.muzafferatmaca.core.common.selfPermission
import com.muzafferatmaca.core.common.setSafeOnClickListener
import com.muzafferatmaca.core.common.showResultDialog
import com.muzafferatmaca.locationtracking.R
import com.muzafferatmaca.locationtracking.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var map: GoogleMap
    @Inject lateinit var userPreferencesViewModel: UserPreferencesViewModel
    @Inject lateinit var locationViewModel: LocationViewModel

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

    }

    private fun setUi() {
        binding.saveButton.primaryTextTextView.text = resources.getString(com.muzafferatmaca.core.R.string.startWalking)
        val supportMapFragment = childFragmentManager.findFragmentByTag("mapFragment") as SupportMapFragment?
        supportMapFragment?.getMapAsync { map = it }
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

    private fun getLocation() {
        lifecycleScope.launch {
            locationViewModel.location.collect {location ->
                location?.let {
                    val (latitude, longitude) = it
                    println(latitude)
                    println(longitude)
                    setLocation(latitude, longitude)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setLocation(latitude : Double, longitude : Double){

        val userLocation = LatLng(latitude, longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(userLocation)
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
            delay(4000)
            map.animateCamera(CameraUpdateFactory.zoomTo(17f), 3000, null)
        }
        setNear(userLocation)
    }

    /**
     * To access their own location again when the user scrolls the map
     */
    private fun setNear(userLocation : LatLng){
        binding.nearMeImageView.setSafeOnClickListener {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17f,), 2000, null)
        }
    }


}