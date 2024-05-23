package com.muzafferatmaca.locationtracking.presentation.home

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.muzafferatmaca.core.baseclass.BaseFragment
import com.muzafferatmaca.locationtracking.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var map : GoogleMap

    override fun initUi() {
        setUi()
    }

    private fun setUi(){
        val supportMapFragment = childFragmentManager.findFragmentByTag("mapFragment") as SupportMapFragment?
        supportMapFragment?.getMapAsync{
            map = it
        }
    }

}