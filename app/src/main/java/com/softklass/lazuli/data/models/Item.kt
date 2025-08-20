package com.softklass.lazuli.data.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "list_item")
data class Item(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    val parent: Int,
    override val content: String,
    val notes: String? = null,
    val notesIsMarkdown: Boolean = false,
    // Serialized drawing strokes (JSON); persisted when user saves without recognizing
    val drawing: String? = null,
    // UTC epoch millis when a reminder should fire; null if no reminder
    val reminderAt: Long? = null,
    val created: String = System.currentTimeMillis().toString(),
) : ListItem
