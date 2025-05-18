package com.softklass.lazuli.ui.detail

import androidx.lifecycle.ViewModel
import com.softklass.lazuli.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository
): ViewModel() {
}
