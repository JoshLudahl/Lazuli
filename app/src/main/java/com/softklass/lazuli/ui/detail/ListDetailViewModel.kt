package com.softklass.lazuli.ui.detail

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.softklass.lazuli.data.device.ReminderScheduler
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.repository.ItemRepository
import com.softklass.lazuli.data.repository.ParentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel
    @Inject
    constructor(
        private val itemRepository: ItemRepository,
        parentRepository: ParentRepository,
        savedStateHandle: SavedStateHandle,
        @ApplicationContext private val appContext: Context,
    ) : ViewModel() {
        private val listId: Int = checkNotNull(savedStateHandle["id"])

        private val _sorted = MutableStateFlow(false)
        val sorted: StateFlow<Boolean>
            get() = _sorted

        private val _listItems = itemRepository.getAllItems(listId)
        val listItems: StateFlow<List<Item?>> =
            _listItems.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

        private val _parent = parentRepository.getParentItem(listId)
        val parent =
            _parent.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1,
            )

        fun addListItem(
            item: String,
            parentId: Int,
        ) {
            viewModelScope.launch {
                itemRepository.addItem(Item(content = item, parent = parentId))
            }
        }

        fun removeItem(item: Item) {
            viewModelScope.launch {
                // Cancel any scheduled reminder for this item before deleting
                ReminderScheduler.cancelReminder(appContext, item.id)
                itemRepository.removeItem(item)
            }
        }

        fun clearList(parentId: Int) {
            viewModelScope.launch {
                // Cancel reminders for all items in this list before deleting them
                listItems.value.filterNotNull().forEach { item ->
                    ReminderScheduler.cancelReminder(appContext, item.id)
                }
                itemRepository.deleteByParent(parentId)
            }
        }

        fun toggleSort() {
            _sorted.value = !_sorted.value
        }

        // OCR processing
        private val textRecognizer: TextRecognizer by lazy {
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        }

        private val _processingImage = MutableStateFlow(false)
        val processingImage: StateFlow<Boolean> = _processingImage

        /**
         * Process the captured image using ML Kit Text Recognition
         * and add each line as a separate item to the list
         */
        suspend fun processImageForOcr(bitmap: Bitmap) {
            _processingImage.value = true
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val result = textRecognizer.process(image).await()

                val textLines = result.textBlocks.flatMap { it.lines }

                // Add each line as a separate item
                textLines.forEach { line ->
                    val text = line.text.trim()
                    if (text.isNotEmpty()) {
                        addListItem(text, listId)
                    }
                }
            } catch (e: Exception) {
                Log.e("ListDetailViewModel", "Error processing image: ${e.message}", e)
            } finally {
                _processingImage.value = false
            }
        }
    }
