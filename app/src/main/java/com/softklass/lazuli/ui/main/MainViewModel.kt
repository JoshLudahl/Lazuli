package com.softklass.lazuli.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.lazuli.data.models.Parent
import com.softklass.lazuli.data.repository.ItemRepository
import com.softklass.lazuli.data.repository.ParentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

        // Loading indicator flips to false after the first emission from DB (even if empty)
        private val _isLoading = kotlinx.coroutines.flow.MutableStateFlow(true)
        val isLoading: kotlinx.coroutines.flow.StateFlow<Boolean> = _isLoading

        init {
            viewModelScope.launch {
                // Wait for the first emission to indicate DB has responded
                _parentItems.first()
                _isLoading.value = false
            }
        }

        // Map of Parent.id -> item count
        @OptIn(ExperimentalCoroutinesApi::class)
        val itemCounts: StateFlow<Map<Int, Int>> =
            parentItems
                .flatMapLatest { parents ->
                    val validParents = parents.filterNotNull()
                    if (validParents.isEmpty()) {
                        flowOf(emptyMap())
                    } else {
                        val flows: List<Flow<Pair<Int, Int>>> =
                            validParents.map { parent ->
                                itemRepository.getAllItems(parent.id).map { items -> parent.id to items.size }
                            }
                        combine(flows) { pairsArray ->
                            pairsArray.toMap()
                        }
                    }
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyMap(),
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
