package com.muzafferatmaca.core.common

import android.app.Activity
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Muzaffer Atmaca on 24.05.2024 at 18:23
 */
fun Activity.selfPermission(
    permission : String,
    view: View,
    snackBarString: CharSequence,
    actionString: CharSequence,
    permissionLauncher : ()-> Unit,
    dialogOrPermissionLauncher : () -> Unit,
    action : () ->Unit,){
    if (ContextCompat.checkSelfPermission(this.baseContext,permission) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Snackbar.make(view, snackBarString, Snackbar.LENGTH_INDEFINITE).setAction(actionString) {
                permissionLauncher()
            }.show()
        } else { dialogOrPermissionLauncher()}
    } else { action() }
}
