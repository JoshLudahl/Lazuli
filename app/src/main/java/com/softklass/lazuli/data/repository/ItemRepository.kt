package com.softklass.lazuli.data.repository

import com.softklass.lazuli.data.database.ItemDao
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val itemDao: ItemDao
) {

}