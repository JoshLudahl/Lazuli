package com.softklass.lazuli.data.repository

import android.util.Log
import com.softklass.lazuli.data.database.ParentDao
import com.softklass.lazuli.data.models.Parent
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "ParentRepository"

class ParentRepository @Inject constructor(
    private val parentDao: ParentDao
) {

    suspend fun getAllListItems() = flow {
        val listItems: List<Parent>

        try {
            Log.d(TAG, "Getting list items")
            listItems = parentDao.getAll()
            emit(listItems)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting list items", e)
            emit(emptyList())
        }
    }

    suspend fun addListItem(parent: Parent) {
        Log.d(TAG, "Adding list item")
        parentDao.upsertAll(parent)
    }
}
