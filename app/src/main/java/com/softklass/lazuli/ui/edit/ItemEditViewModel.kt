package com.softklass.lazuli.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.models.Parent
import com.softklass.lazuli.data.repository.ItemRepository
import com.softklass.lazuli.data.repository.ParentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemEditViewModel @Inject constructor(
    private val parentRepository: ParentRepository,
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: Int = checkNotNull(savedStateHandle["id"])
    private val isParent: Boolean = checkNotNull(savedStateHandle["isParent"])

    private val _item = if (isParent) {
        parentRepository.getParentItem(id)
    } else {
        itemRepository.getItemById(id)
    }
    val item = _item.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun saveItem(content: String) {
        viewModelScope.launch {

            if (isParent) {
                parentRepository.update(
                    Parent(
                        id = id,
                        content = content
                    )
                )
            } else {
                item.value?.let {
                    itemRepository.update(
                        Item(
                            id = it.id,
                            content = content,
                            parent = (it as Item).parent
                        )
                    )
                }
            }
        }
    }
}
