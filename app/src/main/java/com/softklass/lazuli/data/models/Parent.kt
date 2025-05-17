package com.softklass.lazuli.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lists")
data class Parent(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val description: String,
)
