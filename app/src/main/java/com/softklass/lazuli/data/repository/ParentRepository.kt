package com.softklass.lazuli.data.repository

import android.util.Log
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.data.database.ParentDao
import com.softklass.lazuli.data.models.Parent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
}
