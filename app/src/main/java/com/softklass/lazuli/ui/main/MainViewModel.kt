package com.softklass.lazuli.ui.main

import androidx.lifecycle.ViewModel
import com.softklass.lazuli.data.models.Parent

class MainViewModel: ViewModel() {

    fun addList(name: String) {

    }
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val items: List<Parent>) : MainUiState
    data class Error(val message: String) : MainUiState
}
