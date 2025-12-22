package com.softklass.lazuli.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.os.bundleOf
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
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
private sealed interface Navigation : NavKey {
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
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Navigation.Entry)

    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        entryProvider = { key ->
            val navKey = key as Navigation
            when (navKey) {
                is Navigation.Entry ->
                    NavEntry(navKey) {
                        val vm = hiltViewModel<OnboardingViewModel>()
                        LaunchedEffect(Unit) {
                            val done = vm.isOnboardingComplete.first()
                            if (done) {
                                backStack.removeAt(backStack.size - 1)
                                backStack.add(Navigation.Main)
                            } else {
                                backStack.removeAt(backStack.size - 1)
                                backStack.add(Navigation.Onboarding)
                            }
                        }
                    }

                is Navigation.Onboarding ->
                    NavEntry(navKey) {
                        val vm = hiltViewModel<OnboardingViewModel>()
                        OnboardingScreen(
                            onFinished = {
                                vm.setCompleted()
                                backStack.removeAt(backStack.size - 1)
                                backStack.add(Navigation.Main)
                            },
                        )
                    }

                is Navigation.Main ->
                    NavEntry(navKey) {
                        val viewModel = hiltViewModel<MainViewModel>()
                        val didHandleDeepLink = rememberSaveable { mutableStateOf(false) }
                        LaunchedEffect(initialItemId, didHandleDeepLink.value) {
                            val id = initialItemId
                            if (!didHandleDeepLink.value && id != null && id > 0) {
                                didHandleDeepLink.value = true
                                backStack.add(Navigation.ItemView(id))
                            }
                        }
                        val isCompact =
                            windowSizeClass.widthSizeClass == androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact
                        if (isCompact) {
                            Main(
                                viewModel = viewModel,
                                onDetailItemClick = { id ->
                                    backStack.add(Navigation.ListDetail(id))
                                },
                                onEditItemClick = {
                                    backStack.add(Navigation.ItemEdit(it.id))
                                },
                                onSettingsClick = {
                                    backStack.add(Navigation.Settings)
                                },
                                windowSizeClass = windowSizeClass,
                            )
                        } else {
                            val detailBackStack: NavBackStack<NavKey> = rememberNavBackStack(Navigation.Entry)

                            val currentDetailListId = rememberSaveable { mutableStateOf<Int?>(null) }
                            Main(
                                viewModel = viewModel,
                                onDetailItemClick = { id ->
                                    currentDetailListId.value = id
                                    detailBackStack.add(Navigation.ListDetail(id))
                                },
                                onEditItemClick = { item ->
                                    detailBackStack.add(Navigation.ItemEdit(item.id))
                                },
                                onSettingsClick = {
                                    backStack.add(Navigation.Settings)
                                },
                                windowSizeClass = windowSizeClass,
                                onListDeleted = { deletedId ->
                                    if (currentDetailListId.value == deletedId) {
                                        currentDetailListId.value = null
                                        while (detailBackStack.size > 1) {
                                            detailBackStack.removeAt(detailBackStack.size - 1)
                                        }
                                    }
                                },
                                trailingContent = {
                                    NavDisplay(
                                        backStack = detailBackStack,
                                        onBack = { if (detailBackStack.size > 1) detailBackStack.removeAt(detailBackStack.size - 1) },
                                        entryDecorators =
                                            listOf(
                                                rememberSaveableStateHolderNavEntryDecorator(),
                                                rememberViewModelStoreNavEntryDecorator(),
                                            ),
                                        entryProvider = { dKey ->
                                            val detailKey = dKey as Navigation
                                            when (detailKey) {
                                                is Navigation.Entry ->
                                                    NavEntry(detailKey) {
                                                        androidx.compose.material3.Text("Select a list to view its items")
                                                    }
                                                is Navigation.ListDetail ->
                                                    NavEntry(detailKey) {
                                                        val parentViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
                                                        val parentSavedStateRegistryOwner = LocalSavedStateRegistryOwner.current

                                                        val decoratedViewModelStoreOwner =
                                                            remember(detailKey) {
                                                                object :
                                                                    ViewModelStoreOwner,
                                                                    SavedStateRegistryOwner by parentSavedStateRegistryOwner,
                                                                    HasDefaultViewModelProviderFactory {
                                                                    override val viewModelStore: ViewModelStore
                                                                        get() = parentViewModelStoreOwner.viewModelStore

                                                                    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
                                                                        get() = SavedStateViewModelFactory(null, parentSavedStateRegistryOwner, bundleOf("id" to detailKey.id))

                                                                    override val defaultViewModelCreationExtras: androidx.lifecycle.viewmodel.CreationExtras
                                                                        get() {
                                                                            val extras = MutableCreationExtras()
                                                                            extras.set(SAVED_STATE_REGISTRY_OWNER_KEY, parentSavedStateRegistryOwner)
                                                                            extras.set(VIEW_MODEL_STORE_OWNER_KEY, this)
                                                                            extras.set(DEFAULT_ARGS_KEY, bundleOf("id" to detailKey.id))
                                                                            return extras
                                                                        }
                                                                }
                                                            }
                                                        CompositionLocalProvider(LocalViewModelStoreOwner provides decoratedViewModelStoreOwner) {
                                                            val listVm = hiltViewModel<ListDetailViewModel>()
                                                            ListDetailScreen(
                                                                listId = detailKey.id,
                                                                viewModel = listVm,
                                                                onBack = { detailBackStack.removeAt(detailBackStack.size - 1) },
                                                                onEditItemClick = { item ->
                                                                    detailBackStack.add(
                                                                        Navigation.ItemEdit(
                                                                            item.id,
                                                                            isParent = false,
                                                                        ),
                                                                    )
                                                                },
                                                                onViewItemClick = { id ->
                                                                    detailBackStack.add(Navigation.ItemView(id))
                                                                },
                                                            )
                                                        }
                                                    }

                                                is Navigation.ItemView ->
                                                    NavEntry(detailKey) {
                                                        val parentViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
                                                        val parentSavedStateRegistryOwner = LocalSavedStateRegistryOwner.current

                                                        val decoratedViewModelStoreOwner =
                                                            remember(detailKey) {
                                                                object :
                                                                    ViewModelStoreOwner,
                                                                    SavedStateRegistryOwner by parentSavedStateRegistryOwner,
                                                                    HasDefaultViewModelProviderFactory {
                                                                    override val viewModelStore: ViewModelStore
                                                                        get() = parentViewModelStoreOwner.viewModelStore

                                                                    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
                                                                        get() = SavedStateViewModelFactory(null, parentSavedStateRegistryOwner, bundleOf("id" to detailKey.id))

                                                                    override val defaultViewModelCreationExtras: androidx.lifecycle.viewmodel.CreationExtras
                                                                        get() {
                                                                            val extras = MutableCreationExtras()
                                                                            extras.set(SAVED_STATE_REGISTRY_OWNER_KEY, parentSavedStateRegistryOwner)
                                                                            extras.set(VIEW_MODEL_STORE_OWNER_KEY, this)
                                                                            extras.set(DEFAULT_ARGS_KEY, bundleOf("id" to detailKey.id))
                                                                            return extras
                                                                        }
                                                                }
                                                            }
                                                        CompositionLocalProvider(LocalViewModelStoreOwner provides decoratedViewModelStoreOwner) {
                                                            val itemVm = hiltViewModel<com.softklass.lazuli.ui.view.ItemViewViewModel>()
                                                            com.softklass.lazuli.ui.view.ItemViewScreen(
                                                                itemId = detailKey.id,
                                                                viewModel = itemVm,
                                                                onBack = { detailBackStack.removeAt(detailBackStack.size - 1) },
                                                                onEdit = { id ->
                                                                    detailBackStack.add(
                                                                        Navigation.ItemEdit(
                                                                            id,
                                                                            isParent = false,
                                                                        ),
                                                                    )
                                                                },
                                                            )
                                                        }
                                                    }

                                                is Navigation.ItemEdit ->
                                                    NavEntry(detailKey) {
                                                        val parentViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
                                                        val parentSavedStateRegistryOwner = LocalSavedStateRegistryOwner.current

                                                        val decoratedViewModelStoreOwner =
                                                            remember(detailKey) {
                                                                object :
                                                                    ViewModelStoreOwner,
                                                                    SavedStateRegistryOwner by parentSavedStateRegistryOwner,
                                                                    HasDefaultViewModelProviderFactory {
                                                                    override val viewModelStore: ViewModelStore
                                                                        get() = parentViewModelStoreOwner.viewModelStore

                                                                    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
                                                                        get() = SavedStateViewModelFactory(null, parentSavedStateRegistryOwner, bundleOf("id" to detailKey.id, "isParent" to detailKey.isParent))

                                                                    override val defaultViewModelCreationExtras: androidx.lifecycle.viewmodel.CreationExtras
                                                                        get() {
                                                                            val extras = MutableCreationExtras()
                                                                            extras.set(SAVED_STATE_REGISTRY_OWNER_KEY, parentSavedStateRegistryOwner)
                                                                            extras.set(VIEW_MODEL_STORE_OWNER_KEY, this)
                                                                            extras.set(DEFAULT_ARGS_KEY, bundleOf("id" to detailKey.id, "isParent" to detailKey.isParent))
                                                                            return extras
                                                                        }
                                                                }
                                                            }
                                                        CompositionLocalProvider(LocalViewModelStoreOwner provides decoratedViewModelStoreOwner) {
                                                            val editVm = hiltViewModel<ItemEditViewModel>()
                                                            ItemEditScreen(
                                                                viewModel = editVm,
                                                                itemId = detailKey.id,
                                                                onBack = { detailBackStack.removeAt(detailBackStack.size - 1) },
                                                            )
                                                        }
                                                    }
                                                else ->
                                                    NavEntry(detailKey) {
                                                        androidx.compose.material3.Text("Unknown route")
                                                    }
                                            }
                                        },
                                    )
                                },
                            )
                        }
                    }

                is Navigation.ListDetail ->
                    NavEntry(navKey) {
                        val parentViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
                        val parentSavedStateRegistryOwner = LocalSavedStateRegistryOwner.current

                        val decoratedViewModelStoreOwner =
                            remember(navKey) {
                                object :
                                    ViewModelStoreOwner,
                                    SavedStateRegistryOwner by parentSavedStateRegistryOwner,
                                    HasDefaultViewModelProviderFactory {
                                    override val viewModelStore: ViewModelStore
                                        get() = parentViewModelStoreOwner.viewModelStore

                                    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
                                        get() = SavedStateViewModelFactory(null, parentSavedStateRegistryOwner, bundleOf("id" to navKey.id))

                                    override val defaultViewModelCreationExtras: androidx.lifecycle.viewmodel.CreationExtras
                                        get() {
                                            val extras = MutableCreationExtras()
                                            extras.set(SAVED_STATE_REGISTRY_OWNER_KEY, parentSavedStateRegistryOwner)
                                            extras.set(VIEW_MODEL_STORE_OWNER_KEY, this)
                                            extras.set(DEFAULT_ARGS_KEY, bundleOf("id" to navKey.id))
                                            return extras
                                        }
                                }
                            }
                        CompositionLocalProvider(LocalViewModelStoreOwner provides decoratedViewModelStoreOwner) {
                            val viewModel = hiltViewModel<ListDetailViewModel>()
                            ListDetailScreen(
                                listId = navKey.id,
                                viewModel = viewModel,
                                onBack = { backStack.removeAt(backStack.size - 1) },
                                onEditItemClick = { item ->
                                    backStack.add(Navigation.ItemEdit(item.id, isParent = false))
                                },
                                onViewItemClick = { id ->
                                    backStack.add(Navigation.ItemView(id))
                                },
                            )
                        }
                    }

                is Navigation.ItemView ->
                    NavEntry(navKey) {
                        val parentViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
                        val parentSavedStateRegistryOwner = LocalSavedStateRegistryOwner.current

                        val decoratedViewModelStoreOwner =
                            remember(navKey) {
                                object :
                                    ViewModelStoreOwner,
                                    SavedStateRegistryOwner by parentSavedStateRegistryOwner,
                                    HasDefaultViewModelProviderFactory {
                                    override val viewModelStore: ViewModelStore
                                        get() = parentViewModelStoreOwner.viewModelStore

                                    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
                                        get() = SavedStateViewModelFactory(null, parentSavedStateRegistryOwner, bundleOf("id" to navKey.id))

                                    override val defaultViewModelCreationExtras: androidx.lifecycle.viewmodel.CreationExtras
                                        get() {
                                            val extras = MutableCreationExtras()
                                            extras.set(SAVED_STATE_REGISTRY_OWNER_KEY, parentSavedStateRegistryOwner)
                                            extras.set(VIEW_MODEL_STORE_OWNER_KEY, this)
                                            extras.set(DEFAULT_ARGS_KEY, bundleOf("id" to navKey.id))
                                            return extras
                                        }
                                }
                            }
                        CompositionLocalProvider(LocalViewModelStoreOwner provides decoratedViewModelStoreOwner) {
                            val viewModel = hiltViewModel<com.softklass.lazuli.ui.view.ItemViewViewModel>()
                            com.softklass.lazuli.ui.view.ItemViewScreen(
                                itemId = navKey.id,
                                viewModel = viewModel,
                                onBack = { backStack.removeAt(backStack.size - 1) },
                                onEdit = { id ->
                                    backStack.add(
                                        Navigation.ItemEdit(
                                            id,
                                            isParent = false,
                                        ),
                                    )
                                },
                            )
                        }
                    }

                is Navigation.ItemEdit ->
                    NavEntry(navKey) {
                        val parentViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
                        val parentSavedStateRegistryOwner = LocalSavedStateRegistryOwner.current

                        val decoratedViewModelStoreOwner =
                            remember(navKey) {
                                object :
                                    ViewModelStoreOwner,
                                    SavedStateRegistryOwner by parentSavedStateRegistryOwner,
                                    HasDefaultViewModelProviderFactory {
                                    override val viewModelStore: ViewModelStore
                                        get() = parentViewModelStoreOwner.viewModelStore

                                    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
                                        get() = SavedStateViewModelFactory(null, parentSavedStateRegistryOwner, bundleOf("id" to navKey.id, "isParent" to navKey.isParent))

                                    override val defaultViewModelCreationExtras: androidx.lifecycle.viewmodel.CreationExtras
                                        get() {
                                            val extras = MutableCreationExtras()
                                            extras.set(SAVED_STATE_REGISTRY_OWNER_KEY, parentSavedStateRegistryOwner)
                                            extras.set(VIEW_MODEL_STORE_OWNER_KEY, this)
                                            extras.set(DEFAULT_ARGS_KEY, bundleOf("id" to navKey.id, "isParent" to navKey.isParent))
                                            return extras
                                        }
                                }
                            }
                        CompositionLocalProvider(LocalViewModelStoreOwner provides decoratedViewModelStoreOwner) {
                            val viewModel = hiltViewModel<ItemEditViewModel>()
                            ItemEditScreen(
                                viewModel = viewModel,
                                itemId = navKey.id,
                                onBack = { backStack.removeAt(backStack.size - 1) },
                            )
                        }
                    }

                is Navigation.Settings ->
                    NavEntry(navKey) {
                        SettingsScreen(
                            onBack = { backStack.removeAt(backStack.size - 1) },
                        )
                    }
            }
        },
    )
}
