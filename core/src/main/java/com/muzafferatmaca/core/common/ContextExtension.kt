package com.muzafferatmaca.core.common

import android.content.Context
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 14:49
 */
val Context.datastore : DataStore<Preferences> by preferencesDataStore("USER_PREFERENCES")

fun Context.showToast(message: String,duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this,message,duration).show()
}