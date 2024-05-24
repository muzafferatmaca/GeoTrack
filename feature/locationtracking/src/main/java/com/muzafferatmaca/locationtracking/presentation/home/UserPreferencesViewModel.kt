package com.muzafferatmaca.locationtracking.presentation.home

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.viewModelScope
import com.muzafferatmaca.core.baseclass.BaseViewModel
import com.muzafferatmaca.locationtracking.domain.usecase.theme.GetThemeUseCase
import com.muzafferatmaca.locationtracking.domain.usecase.theme.SetThemeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Muzaffer Atmaca on 23.05.2024 at 14:57
 */

class UserPreferencesViewModel @Inject constructor(
    application: Application,
    private val setThemeUseCase: SetThemeUseCase,
    getThemeUseCase: GetThemeUseCase
) : BaseViewModel(application) {

    private val _themeFlow = MutableStateFlow(0)
    val themeFlow: StateFlow<Int> = _themeFlow

    init {
        viewModelScope.launch {
            getThemeUseCase.invoke().collect { theme ->
                _themeFlow.value = theme
            }
        }
    }

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            setThemeUseCase.invoke(theme)
        }
    }

    fun applyTheme() {
        viewModelScope.launch {
            themeFlow.collect { theme ->
                when (theme) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

}