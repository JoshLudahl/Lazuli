package com.softklass.lazuli.data.models

enum class SortOption {
    ASCENDING,
    DESCENDING,
    DATE,
    NONE,
}

fun sortByDate(list: List<Item?>): List<Item?> =
    list.sortedWith { first, second ->
        second?.reminderAt?.let {
            first?.reminderAt?.compareTo(it) ?: 0
        } ?: 0
    }

fun sortAscending(list: List<Item?>): List<Item?> =
    list.sortedWith { first, second ->
        second?.content?.let {
            first?.content?.compareTo(
                it,
                ignoreCase = true,
            )
        } ?: 0
    }

fun sortDescending(list: List<Item?>): List<Item?> = sortAscending(list).reversed()

fun getSortedList(
    sortByOption: SortOption,
    list: List<Item?>,
): List<Item?> = when (sortByOption) {
    SortOption.ASCENDING -> sortAscending(list)
    SortOption.DATE -> sortByDate(list)
    SortOption.DESCENDING -> sortDescending(list)
    SortOption.NONE -> list
}
