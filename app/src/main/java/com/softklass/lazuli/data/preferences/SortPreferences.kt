package com.softklass.lazuli.data.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.softklass.lazuli.data.models.SortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SortPreferences
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        private val sortOptionKey = stringPreferencesKey("list_detail_sort_option")

        val sortOptionPreference: Flow<SortOption> =
            dataStore.data
                .map { preferences ->
                    SortOption.valueOf(preferences[sortOptionKey] ?: SortOption.CREATED.name)
                }.catch { exception ->
                    Log.e("SortPreferences", "Error reading sort option", exception)
                    emit(SortOption.CREATED)
                }

        suspend fun setSortOption(option: SortOption) {
            Log.i("SortPreferences", "setSortOption: $option")
            dataStore.edit { prefs ->
                prefs[sortOptionKey] = option.name
            }
        }
    }
