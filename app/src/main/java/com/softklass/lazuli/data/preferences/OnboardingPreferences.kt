package com.softklass.lazuli.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val DATASTORE_NAME = "lazuli_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

@Singleton
class OnboardingPreferences
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val onboardingCompleteKey = booleanPreferencesKey("onboarding_complete")

        val isOnboardingComplete: Flow<Boolean> =
            context.dataStore.data.map { prefs ->
                prefs[onboardingCompleteKey] ?: false
            }

        suspend fun setOnboardingComplete(value: Boolean) {
            context.dataStore.edit { prefs ->
                prefs[onboardingCompleteKey] = value
            }
        }
    }
