package com.softklass.lazuli.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.lazuli.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val listId: Int = checkNotNull(savedStateHandle["id"])
    private val _listItems = itemRepository.getAllItems(listId)
    val listItems = _listItems.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )
}
