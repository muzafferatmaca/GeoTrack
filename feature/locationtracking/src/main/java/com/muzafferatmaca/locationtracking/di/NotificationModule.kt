package com.muzafferatmaca.locationtracking.di

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.muzafferatmaca.locationtracking.presentation.home.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

/**
 * Created by Muzaffer Atmaca on 25.05.2024 at 19:53
 */
@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    fun providePendingIntent(
        context: Context
    ): PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(
            context,
            Constants.PENDING_INTENT_REQUEST_CODE,
            Intent(context, Class.forName("com.muzafferatmaca.geotrack.MainActivity")),
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        PendingIntent.getActivity(
            context,
            Constants.PENDING_INTENT_REQUEST_CODE,
            Intent(context, Class.forName("com.muzafferatmaca.geotrack.MainActivity")),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(
        context: Context,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setAutoCancel(false)
            .setSmallIcon(com.muzafferatmaca.core.R.drawable.ic_walk)
            .setContentIntent(pendingIntent)

    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        context: Context
    ): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}