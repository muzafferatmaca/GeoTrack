package com.muzafferatmaca.locationtracking.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.muzafferatmaca.locationtracking.presentation.home.Constants
import com.muzafferatmaca.locationtracking.presentation.home.Constants.NOTIFICATION_CHANNEL_ID
import com.muzafferatmaca.locationtracking.presentation.home.Constants.NOTIFICATION_CHANNEL_NAME
import com.muzafferatmaca.locationtracking.presentation.home.Constants.NOTIFICATION_ID
import com.muzafferatmaca.locationtracking.presentation.home.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 24.05.2024 at 19:16
 */
@AndroidEntryPoint
class LocationTrackerService : LifecycleService() {

    @Inject
    lateinit var notification: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject lateinit var locationViewModel: LocationViewModel

    companion object {

        val locationList = MutableLiveData<MutableList<LatLng>>()

    }

    private fun setInitialValues() {

        locationList.postValue(mutableListOf())

    }

    override fun onCreate() {
        super.onCreate()
        setInitialValues()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            stopForegroundService()
            return START_NOT_STICKY
        }

        intent?.let {
            when (it.action) {
                Constants.ACTION_SERVICE_START -> {
                    locationViewModel.startLocationUpdates()
                    startForeGroundService()
                }

                Constants.ACTION_SERVICE_STOP -> {
                    locationViewModel.stopLocationUpdates()
                    stopForegroundService()
                    locationList.value?.clear()
                }

                else -> {}
            }

        }

        lifecycleScope.launch {
            locationViewModel.locationUpdates.collect{
                locationList.value?.apply {
                    add(it)
                    locationList.postValue(this)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun startForeGroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notification.build())
    }

    private fun stopForegroundService() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            NOTIFICATION_ID
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}