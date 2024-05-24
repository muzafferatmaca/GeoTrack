package com.muzafferatmaca.geotrack

import android.view.LayoutInflater
import com.muzafferatmaca.core.baseclass.BaseActivity
import com.muzafferatmaca.geotrack.databinding.ActivityMainBinding
import com.muzafferatmaca.locationtracking.presentation.home.UserPreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var userPreferencesViewModel: UserPreferencesViewModel

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun initUi() {
        userPreferencesViewModel.applyTheme()
    }

}