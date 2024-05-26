package com.muzafferatmaca.core.common

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Created by Muzaffer Atmaca on 25.05.2024 at 23:53
 */
fun ConstraintLayout.visible() {
    this.visibility = View.VISIBLE
}

fun ConstraintLayout.invisible() {
    this.visibility = View.INVISIBLE
}