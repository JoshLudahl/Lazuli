package com.softklass.lazuli.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.softklass.lazuli.data.models.Parent
import kotlinx.coroutines.flow.Flow

@Dao
interface ParentDao {
    @Query("SELECT * FROM lists")
    fun getAll(): Flow<List<Parent?>>

    @Query("SELECT * FROM lists WHERE id = :id")
    fun getById(id: Int): Flow<Parent?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertAll(vararg parents: Parent)

    @Delete
    suspend fun delete(parent: Parent)

    @Query("DELETE FROM lists")
    suspend fun deleteAll()

    @Update
    suspend fun update(parent: Parent)
}
