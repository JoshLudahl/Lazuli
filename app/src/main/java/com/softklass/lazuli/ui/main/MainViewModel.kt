package com.softklass.lazuli.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.lazuli.data.models.Parent
import com.softklass.lazuli.data.repository.ItemRepository
import com.softklass.lazuli.data.repository.ParentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val parentRepository: ParentRepository,
        private val itemRepository: ItemRepository,
    ) : ViewModel() {
        private val _parentItems = parentRepository.getAllListItems()
        val parentItems: StateFlow<List<Parent?>> =
            _parentItems.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

        fun addList(name: String) {
            viewModelScope.launch {
                parentRepository.addListItem(Parent(content = name))
            }
        }

        fun removeItem(item: Parent) {
            viewModelScope.launch {
                parentRepository.removeItem(item)
            }
        }

        fun clearList() {
            viewModelScope.launch {
                parentRepository.clearDatabase()
                itemRepository.clearDatabase()
            }
        }
    }

sealed interface MainUiState {
    data object Loading : MainUiState

    data class Success(
        val items: List<Parent>,
    ) : MainUiState

    data class Error(
        val message: String,
    ) : MainUiState
}
