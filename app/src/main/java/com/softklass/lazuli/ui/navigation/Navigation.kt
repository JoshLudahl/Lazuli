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
import com.softklass.lazuli.ui.main.Main
import com.softklass.lazuli.ui.main.MainViewModel
import kotlinx.serialization.Serializable

@Serializable
private sealed interface Navigation {
    @Serializable
    data object Main : Navigation

    @Serializable
    data class ListDetail(val id: Int) : Navigation
}

@Composable
fun AppNavHost(
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Navigation.Main,
        modifier = Modifier
    ) {
        val animationTween = 350

        composable<Navigation.Main>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(animationTween)
                )
            },
            enterTransition = {
                when (initialState.destination.route) {
                    Navigation.ListDetail.toString() ->  {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(animationTween)
                        )
                    }

                    else -> {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(animationTween)
                        )
                    }
                }

            }

        ) {
            val viewModel = hiltViewModel<MainViewModel>()
            Main(
                viewModel = viewModel,
                onDetailItemClick = { id ->
                    navController.navigate(Navigation.ListDetail(id))
                }
            )
        }

        composable<Navigation.ListDetail>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(animationTween)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(animationTween)
                )
            }
        ) {
            val viewModel = hiltViewModel<ListDetailViewModel>()
            val screen: Navigation.ListDetail = it.toRoute()
            ListDetailScreen(
                listId = screen.id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
