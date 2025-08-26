package com.softklass.lazuli.data.models

enum class SortOption {
    ASCENDING,
    CREATED,
    DESCENDING,
    DATE,
}

fun sortByDate(list: List<Item?>): List<Item?> =
    list.sortedWith { first, second ->
        when {
            first?.reminderAt == null && second?.reminderAt == null -> 0 // Both null, consider them equal
            first?.reminderAt == null -> 1 // First is null, so it should come after non-null second
            second?.reminderAt == null -> -1 // Second is null, so it should come after non-null first
            else -> first.reminderAt.compareTo(second.reminderAt)
        }
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

fun sortByCreated(list: List<Item?>): List<Item?> = list.sortedBy { it?.created }

fun getSortedList(
    sortByOption: SortOption,
    list: List<Item?>,
): List<Item?> =
    when (sortByOption) {
        SortOption.ASCENDING -> sortAscending(list)
        SortOption.CREATED -> sortByCreated(list)
        SortOption.DATE -> sortByDate(list)
        SortOption.DESCENDING -> sortDescending(list)
    }
