package com.softklass.lazuli.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.lazuli.data.models.Parent
import com.softklass.lazuli.data.repository.ParentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemEditViewModel @Inject constructor(
    private val parentRepository: ParentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: Int = checkNotNull(savedStateHandle["id"])

    private val _item = parentRepository.getParentItem(id)
    val item = _item.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun saveItem(content: String) {
        viewModelScope.launch {
            parentRepository.update(
                Parent(
                    id = id,
                    content = content
                )
            )
        }
    }
}
