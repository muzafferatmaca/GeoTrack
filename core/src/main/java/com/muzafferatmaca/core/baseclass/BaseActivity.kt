package com.muzafferatmaca.core.baseclass

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.muzafferatmaca.core.common.applyWindowInsets

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 13:14
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null

    abstract val bindingInflater: (LayoutInflater) -> VB

    val binding get() = _binding!!

    abstract fun initUi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        _binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        binding.root.applyWindowInsets()
        initUi()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}