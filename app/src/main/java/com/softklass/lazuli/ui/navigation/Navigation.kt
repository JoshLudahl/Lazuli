package com.softklass.lazuli.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.softklass.lazuli.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
private sealed interface Navigation {
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
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Navigation.Main,
        modifier = Modifier,
    ) {
        val animationTween = 350
        val slideLeft = AnimatedContentTransitionScope.SlideDirection.Left
        val slideRight = AnimatedContentTransitionScope.SlideDirection.Right

        // Navigation.Main
        composable<Navigation.Main>(
            enterTransition = {
                // When Main becomes the destination (e.g., popping from ListDetail or ItemEdit)
                slideIntoContainer(slideRight, animationSpec = tween(animationTween))
            },
            exitTransition = {
                // When Main is leaving (e.g., navigating to ListDetail or ItemEdit)
                slideOutOfContainer(slideLeft, animationSpec = tween(animationTween))
            },
            popEnterTransition = {
                // When Main is revealed after a pop
                slideIntoContainer(slideRight, animationSpec = tween(animationTween))
            },
            popExitTransition = {
                // When Main is being popped (should not happen if it's the start destination and not on top)
                slideOutOfContainer(slideLeft, animationSpec = tween(animationTween))
            },
        ) {
            val viewModel = hiltViewModel<MainViewModel>()
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
            )
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
                if (targetState.destination.route?.startsWith(Navigation.ItemEdit::class.simpleName.orEmpty()) == true) {
                    slideOutOfContainer(slideRight, animationSpec = tween(animationTween))
                } else {
                    slideOutOfContainer(slideLeft, animationSpec = tween(animationTween))
                }
            },
            popEnterTransition = {
                // When ListDetail is revealed after ItemEdit pops
                slideIntoContainer(slideRight, animationSpec = tween(animationTween))
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
                slideOutOfContainer(slideRight, animationSpec = tween(animationTween))
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
                onEdit = { id -> navController.navigate(Navigation.ItemEdit(id, isParent = false)) },
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
