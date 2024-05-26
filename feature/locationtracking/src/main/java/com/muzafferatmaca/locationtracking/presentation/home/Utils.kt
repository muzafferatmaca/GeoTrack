package com.muzafferatmaca.locationtracking.presentation.home

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.text.DecimalFormat

/**
 * Created by Muzaffer Atmaca on 25.05.2024 at 00:39
 */
object Constants{
    const val ACTION_SERVICE_START = "ACTION_SERVICE_START"
    const val ACTION_SERVICE_STOP = "ACTION_SERVICE_STOP"

    const val PENDING_INTENT_REQUEST_CODE = 100
    const val LOCATION_UPDATE_INTERVAL = 3000L
    const val LOCATION_FASTEST_UPDATE_INTERVAL = 1000L

    const val NOTIFICATION_ID = 7
    const val NOTIFICATION_CHANNEL_ID = "notification_id"
    const val NOTIFICATION_CHANNEL_NAME = "notification_name"
}

fun LatLng.setCameraPosition(): CameraPosition {
    return CameraPosition.Builder()
        .target(this)
        .zoom(17f)
        .build()
}

fun calculateTheDistance(locationList: MutableList<LatLng>): String {
    if(locationList.size > 1){
        val meters = SphericalUtil.computeDistanceBetween(locationList.first(), locationList.last())
        return DecimalFormat("#.##").format(meters)
    }
    return "0.00"
}