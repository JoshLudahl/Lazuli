package com.softklass.lazuli.data.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingPreferences
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        private val onboardingCompleteKey = booleanPreferencesKey("onboarding_complete")

        val isOnboardingComplete: Flow<Boolean> =
            dataStore.data.map { prefs ->
                Log.i("OnboardingPreferences", "isOnboardingComplete: ${prefs[onboardingCompleteKey]}")
                prefs[onboardingCompleteKey] ?: false
            }

        suspend fun setOnboardingComplete(value: Boolean) {
            Log.i("OnboardingPreferences", "setOnboardingComplete: $value")
            dataStore.edit { prefs ->
                prefs[onboardingCompleteKey] = value
            }
        }
    }
