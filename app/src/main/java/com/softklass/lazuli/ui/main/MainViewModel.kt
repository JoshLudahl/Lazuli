package com.softklass.lazuli.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.lazuli.data.models.Parent
import com.softklass.lazuli.data.repository.ParentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val parentRepository: ParentRepository
): ViewModel() {

    private val _parentItems = MutableStateFlow<List<Parent?>>(emptyList())
    val parentItems: StateFlow<List<Parent?>>
        get() = _parentItems

    init {
        viewModelScope.launch {
            parentRepository.getAllListItems().collectLatest { value ->
                _parentItems.update { value }
            }
        }
    }

    fun addList(name: String) {
        viewModelScope.launch {
            parentRepository.addListItem(Parent(description = name))
        }
    }
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val items: List<Parent>) : MainUiState
    data class Error(val message: String) : MainUiState
}
