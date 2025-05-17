package com.softklass.lazuli.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_item")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val parent: String,
    val content: String,
    val created: String,
)
