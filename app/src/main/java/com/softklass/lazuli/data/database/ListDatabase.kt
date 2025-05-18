package com.softklass.lazuli.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.models.Parent

@Database(
    entities = [
        Item::class,
        Parent::class
    ],
    version = 1,
    exportSchema = false,
)
abstract class ListDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    abstract fun parentDao(): ParentDao
}
