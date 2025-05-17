package com.softklass.lazuli.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.softklass.lazuli.data.models.Parent

@Dao
interface ParentDao {

    @Query("SELECT * FROM lists")
    fun getAll(): List<Parent>

    @Upsert
    fun upsertAll(vararg parents: Parent)

    @Delete
    fun delete(parent: Parent)
}
