package com.softklass.lazuli.ui.edit

import androidx.lifecycle.ViewModel
import com.softklass.lazuli.data.database.ParentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItemEditViewModel @Inject constructor(
    private val parentDao: ParentDao
): ViewModel() {

    fun saveItem() {

    }
}
