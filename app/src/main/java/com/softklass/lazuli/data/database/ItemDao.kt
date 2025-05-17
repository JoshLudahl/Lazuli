package com.softklass.lazuli.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.softklass.lazuli.data.models.Item

@Dao
interface ItemDao {

    @Query("SELECT * FROM list_item WHERE parent = :parent")
    fun getAll(parent: Int): List<Item>

    @Upsert
    fun upsertAll(vararg items: Item)

    @Delete
    fun delete(item: Item)
}
