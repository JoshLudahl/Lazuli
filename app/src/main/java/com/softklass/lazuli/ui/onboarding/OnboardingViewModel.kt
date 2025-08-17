package com.softklass.lazuli.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.lazuli.data.preferences.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val prefs: OnboardingPreferences,
    ) : ViewModel() {
        // Expose the cold Flow directly so callers can await the first real value from DataStore.
        val isOnboardingComplete: Flow<Boolean> = prefs.isOnboardingComplete

        fun setCompleted() {
            viewModelScope.launch {
                prefs.setOnboardingComplete(true)
            }
        }
    }
