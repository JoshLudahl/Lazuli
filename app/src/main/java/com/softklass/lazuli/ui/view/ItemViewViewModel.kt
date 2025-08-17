package com.softklass.lazuli.ui.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ItemViewViewModel
    @Inject
    constructor(
        itemRepository: ItemRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val id: Int = checkNotNull(savedStateHandle["id"]) // Navigation argument

        val item: StateFlow<Item?> =
            itemRepository
                .getItemById(id)
                .map { it }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null,
                )
    }
