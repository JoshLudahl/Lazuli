package com.softklass.lazuli.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.softklass.lazuli.ui.detail.ListDetailScreen
import com.softklass.lazuli.ui.detail.ListDetailViewModel
import com.softklass.lazuli.ui.edit.ItemEditScreen
import com.softklass.lazuli.ui.edit.ItemEditViewModel
import com.softklass.lazuli.ui.main.Main
import com.softklass.lazuli.ui.main.MainViewModel
import com.softklass.lazuli.ui.onboarding.OnboardingScreen
import com.softklass.lazuli.ui.onboarding.OnboardingViewModel
import com.softklass.lazuli.ui.settings.SettingsScreen
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@Serializable
private sealed interface Navigation {
    @Serializable
    data object Entry : Navigation

    @Serializable
    data object Onboarding : Navigation

    @Serializable
    data object Main : Navigation

    @Serializable
    data class ListDetail(
        val id: Int,
    ) : Navigation

    @Serializable
    data class ItemView(
        val id: Int,
    ) : Navigation

    @Serializable
    data class ItemEdit(
        val id: Int,
        val isParent: Boolean = true,
    ) : Navigation

    @Serializable
    data object Settings : Navigation
}

@Composable
fun AppNavHost(
    initialItemId: Int? = null,
    windowSizeClass: androidx.compose.material3.windowsizeclass.WindowSizeClass,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.Entry,
        modifier = Modifier,
    ) {
        val animationTween = 350
        val slideLeft = AnimatedContentTransitionScope.SlideDirection.Left
        val slideRight = AnimatedContentTransitionScope.SlideDirection.Right

        // Navigation.Entry
        composable<Navigation.Entry>(
            enterTransition = { fadeIn(animationSpec = tween(animationTween)) },
            exitTransition = { fadeOut(animationSpec = tween(animationTween)) },
            popEnterTransition = { fadeIn(animationSpec = tween(animationTween)) },
            popExitTransition = { fadeOut(animationSpec = tween(animationTween)) },
        ) {
            val vm = hiltViewModel<OnboardingViewModel>()
            // Await the first real value from DataStore before deciding where to go.
            LaunchedEffect(Unit) {
                val done = vm.isOnboardingComplete.first()
                if (done) {
                    navController.navigate(Navigation.Main) {
                        popUpTo(Navigation.Entry) { inclusive = true }
                    }
                } else {
                    navController.navigate(Navigation.Onboarding) {
                        popUpTo(Navigation.Entry) { inclusive = true }
                    }
                }
            }
        }

        // Navigation.Onboarding
        composable<Navigation.Onboarding>(
            enterTransition = { fadeIn(animationSpec = tween(animationTween)) },
            exitTransition = { fadeOut(animationSpec = tween(animationTween)) },
            popEnterTransition = { fadeIn(animationSpec = tween(animationTween)) },
            popExitTransition = { fadeOut(animationSpec = tween(animationTween)) },
        ) {
            val vm = hiltViewModel<OnboardingViewModel>()
            OnboardingScreen(
                onFinished = {
                    vm.setCompleted()
                    navController.navigate(Navigation.Main) {
                        popUpTo(Navigation.Onboarding) { inclusive = true }
                    }
                },
            )
        }

        // Navigation.Main
        composable<Navigation.Main>(
            enterTransition = {
                // If coming from Onboarding, fade in; otherwise keep default slide.
                if (initialState.destination.route?.contains(Navigation.Onboarding::class.simpleName.toString()) == true ||
                    initialState.destination.route?.contains(Navigation.Entry::class.simpleName.toString()) == true
                ) {
                    fadeIn(animationSpec = tween(animationTween))
                } else {
                    // When Main becomes the destination (e.g., popping from ListDetail or ItemEdit)
                    slideIntoContainer(slideRight, animationSpec = tween(animationTween))
                }
            },
            exitTransition = {
                // When Main is leaving (e.g., navigating to ListDetail or ItemEdit)
                slideOutOfContainer(slideLeft, animationSpec = tween(animationTween))
            },
            popEnterTransition = {
                // If coming from Onboarding, fade in; otherwise keep default slide.
                if (initialState.destination.route?.startsWith(Navigation.Onboarding::class.simpleName.toString()) == true ||
                    initialState.destination.route?.startsWith(Navigation.Entry::class.simpleName.toString()) == true
                ) {
                    fadeIn(animationSpec = tween(animationTween))
                } else {
                    // When Main becomes the destination (e.g., popping from ListDetail or ItemEdit)
                    slideIntoContainer(slideRight, animationSpec = tween(animationTween))
                }
            },
            popExitTransition = {
                // When Main is being popped (should not happen if it's the start destination and not on top)
                slideOutOfContainer(slideLeft, animationSpec = tween(animationTween))
            },
        ) {
            val viewModel = hiltViewModel<MainViewModel>()
            // If launched from a reminder notification, navigate to the item's view screen once.
            val didHandleDeepLink = rememberSaveable { mutableStateOf(false) }
            LaunchedEffect(initialItemId, didHandleDeepLink.value) {
                val id = initialItemId
                if (!didHandleDeepLink.value && id != null && id > 0) {
                    didHandleDeepLink.value = true
                    navController.navigate(Navigation.ItemView(id))
                }
            }
            val isCompact =
                windowSizeClass.widthSizeClass == androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact
            if (isCompact) {
                Main(
                    viewModel = viewModel,
                    onDetailItemClick = { id ->
                        navController.navigate(Navigation.ListDetail(id))
                    },
                    onEditItemClick = {
                        navController.navigate(Navigation.ItemEdit(it.id))
                    },
                    onSettingsClick = {
                        navController.navigate(Navigation.Settings)
                    },
                    windowSizeClass = windowSizeClass,
                )
            } else {
                val detailNavController = rememberNavController()
                val currentDetailListId = rememberSaveable { mutableStateOf<Int?>(null) }
                Main(
                    viewModel = viewModel,
                    onDetailItemClick = { id ->
                        currentDetailListId.value = id
                        detailNavController.navigate(Navigation.ListDetail(id))
                    },
                    onEditItemClick = { item ->
                        // Edit parent/list on the right pane as well
                        detailNavController.navigate(Navigation.ItemEdit(item.id))
                    },
                    onSettingsClick = {
                        // Settings remains a full-screen destination on the root controller
                        navController.navigate(Navigation.Settings)
                    },
                    windowSizeClass = windowSizeClass,
                    onListDeleted = { deletedId ->
                        if (currentDetailListId.value == deletedId) {
                            // Reset and pop detail pane back to placeholder when the displayed list is removed
                            currentDetailListId.value = null
                            detailNavController.popBackStack("placeholder", false)
                        }
                    },
                    trailingContent = {
                        NavHost(
                            navController = detailNavController,
                            startDestination = "placeholder",
                        ) {
                            composable("placeholder") {
                                androidx.compose.material3.Text("Select a list to view its items")
                            }
                            composable<Navigation.ListDetail>(
                                enterTransition = {
                                    // Fade-through for two-pane: incoming fades/scale in after outgoing fades out
                                    fadeIn(
                                        animationSpec = tween(
                                            durationMillis = 210,
                                            delayMillis = 90
                                        )
                                    ) +
                                            scaleIn(
                                                animationSpec = tween(
                                                    durationMillis = 210,
                                                    delayMillis = 90
                                                ), initialScale = 0.92f
                                            )
                                },
                                exitTransition = {
                                    // Outgoing content just fades out quickly
                                    fadeOut(animationSpec = tween(durationMillis = 90))
                                },
                                popEnterTransition = {
                                    fadeIn(
                                        animationSpec = tween(
                                            durationMillis = 210,
                                            delayMillis = 90
                                        )
                                    ) +
                                            scaleIn(
                                                animationSpec = tween(
                                                    durationMillis = 210,
                                                    delayMillis = 90
                                                ), initialScale = 0.92f
                                            )
                                },
                                popExitTransition = {
                                    fadeOut(animationSpec = tween(durationMillis = 90))
                                },
                            ) {
                                val listVm = hiltViewModel<ListDetailViewModel>()
                                val screen: Navigation.ListDetail = it.toRoute()
                                ListDetailScreen(
                                    listId = screen.id,
                                    viewModel = listVm,
                                    onBack = { detailNavController.popBackStack() },
                                    onEditItemClick = { item ->
                                        detailNavController.navigate(
                                            Navigation.ItemEdit(
                                                item.id,
                                                isParent = false
                                            )
                                        )
                                    },
                                    onViewItemClick = { id ->
                                        detailNavController.navigate(Navigation.ItemView(id))
                                    },
                                )
                            }
                            composable<Navigation.ItemView>(
                                enterTransition = {
                                    fadeIn(
                                        animationSpec = tween(
                                            durationMillis = 210,
                                            delayMillis = 90
                                        )
                                    ) +
                                            scaleIn(
                                                animationSpec = tween(
                                                    durationMillis = 210,
                                                    delayMillis = 90
                                                ), initialScale = 0.92f
                                            )
                                },
                                exitTransition = {
                                    fadeOut(animationSpec = tween(durationMillis = 90))
                                },
                                popEnterTransition = {
                                    fadeIn(
                                        animationSpec = tween(
                                            durationMillis = 210,
                                            delayMillis = 90
                                        )
                                    ) +
                                            scaleIn(
                                                animationSpec = tween(
                                                    durationMillis = 210,
                                                    delayMillis = 90
                                                ), initialScale = 0.92f
                                            )
                                },
                                popExitTransition = {
                                    fadeOut(animationSpec = tween(durationMillis = 90))
                                },
                            ) {
                                val screen: Navigation.ItemView = it.toRoute()
                                com.softklass.lazuli.ui.view.ItemViewScreen(
                                    itemId = screen.id,
                                    onBack = { detailNavController.popBackStack() },
                                    onEdit = { id ->
                                        detailNavController.navigate(
                                            Navigation.ItemEdit(
                                                id,
                                                isParent = false
                                            )
                                        )
                                    },
                                )
                            }
                            composable<Navigation.ItemEdit>(
                                enterTransition = {
                                    fadeIn(
                                        animationSpec = tween(
                                            durationMillis = 210,
                                            delayMillis = 90
                                        )
                                    ) +
                                            scaleIn(
                                                animationSpec = tween(
                                                    durationMillis = 210,
                                                    delayMillis = 90
                                                ), initialScale = 0.92f
                                            )
                                },
                                exitTransition = {
                                    fadeOut(animationSpec = tween(durationMillis = 90))
                                },
                                popEnterTransition = {
                                    fadeIn(
                                        animationSpec = tween(
                                            durationMillis = 210,
                                            delayMillis = 90
                                        )
                                    ) +
                                            scaleIn(
                                                animationSpec = tween(
                                                    durationMillis = 210,
                                                    delayMillis = 90
                                                ), initialScale = 0.92f
                                            )
                                },
                                popExitTransition = {
                                    fadeOut(animationSpec = tween(durationMillis = 90))
                                },
                            ) {
                                val screen: Navigation.ItemEdit = it.toRoute()
                                val editVm = hiltViewModel<ItemEditViewModel>()
                                ItemEditScreen(
                                    viewModel = editVm,
                                    itemId = screen.id,
                                    onBack = { detailNavController.popBackStack() },
                                )
                            }
                        }
                    },
                )
            }
        }

        // Navigation.ListDetail
        composable<Navigation.ListDetail>(
            enterTransition = {
                // When ListDetail becomes the destination (from Main)
                slideIntoContainer(slideLeft, animationSpec = tween(animationTween))
            },
            exitTransition = {
                // When ListDetail is leaving
                // If going to ItemEdit, ListDetail slides Left
                // If going back to Main, ListDetail slides Right
                if (targetState.destination.route?.startsWith(Navigation.ItemEdit::class.simpleName.toString()) == true) {
                    slideOutOfContainer(slideRight, animationSpec = tween(animationTween))
                } else {
                    slideOutOfContainer(slideLeft, animationSpec = tween(animationTween))
                }
            },
            popEnterTransition = {
                if (initialState.destination.route?.startsWith(Navigation.ItemEdit::class.simpleName.toString()) == true) {
                    slideIntoContainer(slideRight, animationSpec = tween(animationTween))
                } else {
                    slideIntoContainer(slideRight, animationSpec = tween(animationTween))
                }
            },
            popExitTransition = {
                // When ListDetail is popped (back to Main)
                slideOutOfContainer(slideRight, animationSpec = tween(animationTween))
            },
        ) {
            val viewModel = hiltViewModel<ListDetailViewModel>()
            val screen: Navigation.ListDetail = it.toRoute()
            ListDetailScreen(
                listId = screen.id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEditItemClick = { item ->
                    navController.navigate(Navigation.ItemEdit(item.id, isParent = false))
                },
                onViewItemClick = { id ->
                    navController.navigate(Navigation.ItemView(id))
                },
            )
        }

        // Navigation.ItemView
        composable<Navigation.ItemView>(
            enterTransition = {
                slideIntoContainer(slideLeft, animationSpec = tween(animationTween))
            },
            exitTransition = {
                slideOutOfContainer(slideLeft, animationSpec = tween(animationTween))
            },
            popEnterTransition = {
                slideIntoContainer(slideRight, animationSpec = tween(animationTween))
            },
            popExitTransition = {
                slideOutOfContainer(slideRight, animationSpec = tween(animationTween))
            },
        ) {
            val screen: Navigation.ItemView = it.toRoute()
            com.softklass.lazuli.ui.view.ItemViewScreen(
                itemId = screen.id,
                onBack = { navController.popBackStack() },
                onEdit = { id ->
                    navController.navigate(
                        Navigation.ItemEdit(
                            id,
                            isParent = false
                        )
                    )
                },
            )
        }

        // Navigation.ItemEdit
        composable<Navigation.ItemEdit>(
            enterTransition = {
                // When ItemEdit becomes the destination (from Main or ListDetail)
                slideIntoContainer(slideLeft, animationSpec = tween(animationTween))
            },
            exitTransition = {
                // When ItemEdit is leaving (should only be on pop back)
                slideOutOfContainer(slideRight, animationSpec = tween(animationTween))
            },
            popEnterTransition = {
                // When ItemEdit is revealed after a pop (should not happen if it's not a host)
                slideIntoContainer(slideLeft, animationSpec = tween(animationTween))
            },
            popExitTransition = {
                // When ItemEdit is popped (back to Main or ListDetail)
                slideOutOfContainer(slideRight, animationSpec = tween(animationTween))
            },
        ) {
            val screen: Navigation.ItemEdit = it.toRoute()
            val viewModel = hiltViewModel<ItemEditViewModel>()
            ItemEditScreen(
                viewModel = viewModel,
                itemId = screen.id,
                onBack = { navController.popBackStack() },
            )
        }

        // Navigation.Settings
        composable<Navigation.Settings>(
            enterTransition = {
                // When Settings becomes the destination (from Main)
                slideIntoContainer(slideLeft, animationSpec = tween(animationTween))
            },
            exitTransition = {
                // When Settings is leaving (back to Main)
                slideOutOfContainer(slideRight, animationSpec = tween(animationTween))
            },
            popEnterTransition = {
                // When Settings is revealed after a pop (should not happen)
                slideIntoContainer(slideLeft, animationSpec = tween(animationTween))
            },
            popExitTransition = {
                // When Settings is popped (back to Main)
                slideOutOfContainer(slideRight, animationSpec = tween(animationTween))
            },
        ) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
