package com.softklass.lazuli.data.repository

import com.softklass.lazuli.data.database.ParentDao
import com.softklass.lazuli.data.models.Parent
import javax.inject.Inject

class ParentRepository @Inject constructor(
    private val parentDao: ParentDao
) {
    fun getAllListItems() = parentDao.getAll()

    suspend fun addListItem(parent: Parent) = parentDao.upsertAll(parent)

    fun getParentItem(id: Int) = parentDao.getById(id)

    suspend fun removeItem(item: Parent) = parentDao.delete(item)

    suspend fun clearDatabase() = parentDao.deleteAll()

    suspend fun update(item: Parent) = parentDao.update(item)
}
