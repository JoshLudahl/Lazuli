package com.softklass.lazuli

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule

abstract class BaseTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule(MainActivity::class.java)
}
