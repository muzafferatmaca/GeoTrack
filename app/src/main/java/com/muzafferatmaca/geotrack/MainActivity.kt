package com.muzafferatmaca.geotrack

import android.view.LayoutInflater
import com.muzafferatmaca.core.baseclass.BaseActivity
import com.muzafferatmaca.geotrack.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun initUi() {}

}