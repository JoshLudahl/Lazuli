package com.softklass.lazuli.data.models

import java.time.Instant

data class Item(
    val content: String,
    val added: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
    val category: List<Category>,
    val label: List<Label>
)
