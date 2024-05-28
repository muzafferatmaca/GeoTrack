package com.muzafferatmaca.core.common

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.muzafferatmaca.core.R
import com.muzafferatmaca.core.databinding.DialogUtilBinding

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 14:49
 */
val Context.datastore : DataStore<Preferences> by preferencesDataStore("USER_PREFERENCES")

fun Context.showToast(message: String,duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this,message,duration).show()
}

fun Context.redirectToSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

fun Context.showResultDialog(
    lottieFile : Int,
    isLottie : Boolean,
    titleText: String,
    titleTextColor: Int,
    descriptionText: String,
    descriptionColor: Int,
    yesButtonText: String,
    yesButtonDrawable: Drawable,
    noButtonText: String,
    noButtonDrawable: Drawable,
    cancelable :Boolean,
    isOnClickVisibility : Boolean,
    onClickNo: () -> Unit,
    onClickYes: () -> Unit
) {
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_util, null, false)
    val dialog = DialogUtilBinding.bind(view)
    val builder = Dialog(this)
    builder.setContentView(view)
    builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    builder.setCancelable(cancelable)
    builder.setCanceledOnTouchOutside(cancelable)
    builder.show()

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(builder.window!!.attributes)
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    val window = builder.window
    window!!.attributes = lp
    //window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    if(isLottie){
        dialog.notificationLottie.visible()
        dialog.notificationLottie.setAnimation(lottieFile)
        dialog.notificationLottie.playAnimation()
    }else{
        dialog.notificationLottie.gone()
    }

    dialog.titleTextView.text = titleText
    dialog.titleTextView.setTextColor(titleTextColor)

    dialog.descriptionTextView.text = descriptionText
    dialog.descriptionTextView.setTextColor(descriptionColor)

    dialog.yesButton.background = yesButtonDrawable
    dialog.yesButtonTextView.text = yesButtonText

    dialog.noButton.background = noButtonDrawable
    dialog.noButtonTextView.text = noButtonText

    if (isOnClickVisibility){
        dialog.yesButton.visible()
        dialog.setupYesButtonClickListener(builder,onClickYes)
    }else{
        dialog.yesButton.gone()
    }

    dialog.setupNoButtonClickListener(builder,onClickNo)
}

fun DialogUtilBinding.setupYesButtonClickListener(dialog: Dialog, onClick: () -> Unit) {
    yesButton.setOnClickListener {
        onClick()
        dialog.dismiss()
    }
}

fun DialogUtilBinding.setupNoButtonClickListener(dialog: Dialog, onClick: () -> Unit) {
    noButton.setOnClickListener {
        onClick()
        dialog.dismiss()
    }
}