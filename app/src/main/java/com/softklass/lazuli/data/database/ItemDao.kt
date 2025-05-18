package com.softklass.lazuli.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.softklass.lazuli.data.models.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM list_item WHERE parent = :parent")
    fun getAll(parent: Int): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItem(vararg items: Item)

    @Delete
    fun delete(item: Item)
}
