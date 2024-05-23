package com.muzafferatmaca.locationtracking.presentation.home

import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.muzafferatmaca.core.baseclass.BaseFragment
import com.muzafferatmaca.core.common.setSafeOnClickListener
import com.muzafferatmaca.core.common.showToast
import com.muzafferatmaca.locationtracking.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var map: GoogleMap

    @Inject
    lateinit var homeViewModel: HomeViewModel

    override fun initUi() {
        setTheme()
        observeTheme()
        setUi()
    }

    private fun setUi() {
        val supportMapFragment =
            childFragmentManager.findFragmentByTag("mapFragment") as SupportMapFragment?
        supportMapFragment?.getMapAsync {
            map = it
        }
    }

    private fun setTheme() {
        binding.uiMode.setSafeOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val currentTheme = homeViewModel.themeFlow.value
                val newTheme = when (currentTheme) {
                    0, 1 -> 2
                    2 -> 1
                    else -> 0
                }
                homeViewModel.setTheme(newTheme)
            }
        }
    }

    private fun observeTheme() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.themeFlow.collect { theme ->
                when (theme) {
                    0 -> binding.uiMode.setImageResource(com.muzafferatmaca.core.R.drawable.ic_mode_light)
                    1 -> binding.uiMode.setImageResource(com.muzafferatmaca.core.R.drawable.ic_mode_night)
                    2 -> binding.uiMode.setImageResource(com.muzafferatmaca.core.R.drawable.ic_mode_light)
                }
            }
        }
    }

}