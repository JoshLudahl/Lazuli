package com.softklass.lazuli.data.repository

import com.softklass.lazuli.data.database.ItemDao
import com.softklass.lazuli.data.models.Item
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val itemDao: ItemDao
) {
    fun getAllItems(parent: Int) = itemDao.getAll(parent)

    suspend fun addItem(item: Item) = itemDao.addItem(item)

    suspend fun removeItem(item: Item) = itemDao.delete(item)

}
