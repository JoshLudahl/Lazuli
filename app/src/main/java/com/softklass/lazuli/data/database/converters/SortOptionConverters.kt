package com.softklass.lazuli.data.database.converters

import android.util.Log
import androidx.room.TypeConverter
import com.softklass.lazuli.data.models.SortOption

class SortOptionConverters {
    @TypeConverter
    fun fromSortOption(option: SortOption?): String? = option?.name

    @TypeConverter
    fun toSortOption(name: String?): SortOption =
        try {
            if (name == null) SortOption.CREATED else SortOption.valueOf(name)
        } catch (e: IllegalArgumentException) {
            Log.e("SortOptionConverters", "Invalid SortOption name: $name, defaulting to Created at.\nError: $e")
            SortOption.CREATED
        }
}
