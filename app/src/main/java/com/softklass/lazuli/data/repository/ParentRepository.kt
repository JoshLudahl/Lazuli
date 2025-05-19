package com.softklass.lazuli.data.repository

import android.util.Log
import com.softklass.lazuli.data.database.ParentDao
import com.softklass.lazuli.data.models.Parent
import javax.inject.Inject

private const val TAG = "ParentRepository"

class ParentRepository @Inject constructor(
    private val parentDao: ParentDao
) {
    fun getAllListItems() = parentDao.getAll()

    suspend fun addListItem(parent: Parent) {
        Log.d(TAG, "Adding list item")
        parentDao.upsertAll(parent)
    }

    fun getParentItem(id: Int) = parentDao.getById(id)

    suspend fun removeItem(item: Parent) {
        Log.d(TAG, "Removing list item")
        parentDao.delete(item)
    }

    suspend fun removeAll() {
        Log.d(TAG, "Removing all list items")
        parentDao.deleteAll()
    }
}
