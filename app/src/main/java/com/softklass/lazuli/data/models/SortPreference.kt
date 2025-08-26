package com.softklass.lazuli.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sort_preference")
data class SortPreference(
    @PrimaryKey val id: Int,
    val sortOption: SortOption = SortOption.CREATED,
)
