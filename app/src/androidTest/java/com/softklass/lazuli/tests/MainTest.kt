package com.softklass.lazuli.tests

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.softklass.lazuli.BaseTest
import com.softklass.lazuli.screens.MainScreen
import org.junit.Test

class MainTest : BaseTest() {

    @Test
    fun titleIsDisplayed() {
        composeTestRule.onNodeWithTag(MainScreen.TITLE.tag).isDisplayed()
    }

    @Test
    fun canEnterListItem() {
        val input = "Test"

        composeTestRule
            .onNodeWithTag(MainScreen.LIST_ENTRY.tag)
            .performTextInput(input)

        composeTestRule
            .onNodeWithTag(MainScreen.LIST_ENTRY_BUTTON.tag)
            .performClick()

        composeTestRule
            .onNodeWithTag(MainScreen.CLEAR.tag)
            .isDisplayed()

        composeTestRule.onNodeWithTag(input).isDisplayed()
    }
}
