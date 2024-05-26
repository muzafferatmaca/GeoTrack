package com.muzafferatmaca.locationtracking.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.muzafferatmaca.locationtracking.presentation.home.Constants
import com.muzafferatmaca.locationtracking.presentation.home.Constants.NOTIFICATION_CHANNEL_ID
import com.muzafferatmaca.locationtracking.presentation.home.Constants.NOTIFICATION_CHANNEL_NAME
import com.muzafferatmaca.locationtracking.presentation.home.Constants.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
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

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val started = MutableLiveData<Boolean>()
        val startTime = MutableLiveData<Long>()
        val stopTime = MutableLiveData<Long>()

        val locationList = MutableLiveData<MutableList<LatLng>>()
    }

    private fun setInitialValues() {
        started.postValue(false)
        startTime.postValue(0L)
        stopTime.postValue(0L)

        locationList.postValue(mutableListOf())
    }

    override fun onCreate() {
        setInitialValues()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                Constants.ACTION_SERVICE_START -> {
                    started.postValue(true)
                    startForeGroundService()
                }

                Constants.ACTION_SERVICE_STOP -> {
                    started.postValue(false)
                    stopForegroundService()
                }

                else -> {}
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
        stopTime.postValue(System.currentTimeMillis())
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