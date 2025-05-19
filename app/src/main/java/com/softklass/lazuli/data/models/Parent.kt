package com.softklass.lazuli.data.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "lists")
data class Parent(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    override val content: String
) : ListItem
