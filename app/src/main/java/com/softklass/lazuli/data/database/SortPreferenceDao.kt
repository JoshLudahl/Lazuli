package com.softklass.lazuli.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.softklass.lazuli.data.models.SortPreference
import kotlinx.coroutines.flow.Flow

@Dao
interface SortPreferenceDao {
    @Query("SELECT * FROM sort_preference WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<SortPreference?>

    @Upsert
    suspend fun upsert(pref: SortPreference)
}
