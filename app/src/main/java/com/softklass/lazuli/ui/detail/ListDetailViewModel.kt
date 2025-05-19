package com.softklass.lazuli.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.repository.ItemRepository
import com.softklass.lazuli.data.repository.ParentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    parentRepository: ParentRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val listId: Int = checkNotNull(savedStateHandle["id"])

    private val _listItems = itemRepository.getAllItems(listId)
    val listItems: StateFlow<List<Item?>> = _listItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _parent = parentRepository.getParentItem(listId)
    val parent = _parent.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    fun addListItem(item: String, parentId: Int) {
        viewModelScope.launch {
            itemRepository.addItem(Item(content = item, parent = parentId))
        }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            itemRepository.removeItem(item)
        }
    }
}
