package com.softklass.lazuli.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
                Log.i("OnboardingPreferences", "isOnboardingComplete: ${prefs[onboardingCompleteKey]}")
                prefs[onboardingCompleteKey] ?: false
            }

        fun readBooleanDataStoreFlow(
            key: String,
            dataStore: DataStore<Preferences>,
        ): Flow<Boolean> =
            dataStore.data
                .catch {
                    emit(emptyPreferences())
                }.map { preferences ->
                    preferences[booleanPreferencesKey(key)] ?: false
                }

        val isOnboardingComplete2: Flow<Boolean> =
            context.dataStore.data
                .catch {
                    Log.i("OnboardingPreferences", "isOnboardingComplete: emptyPreferences")

                    emit(emptyPreferences())
                }.map { preferences ->
                    Log.i("OnboardingPreferences", "isOnboardingComplete: ${preferences[onboardingCompleteKey]}")
                    preferences[onboardingCompleteKey] ?: false
                }

        suspend fun setOnboardingComplete(value: Boolean) {
            Log.i("OnboardingPreferences", "setOnboardingComplete: $value")
            context.dataStore.edit { prefs ->
                prefs[onboardingCompleteKey] = value
            }
        }
    }
