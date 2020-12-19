package jp.senchan.android.wasatter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import jp.senchan.android.wasatter.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import timber.log.Timber

fun NavGraphBuilder.screenComposable(
    screen: Screen,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit,
) = composable(
    screen.path,
    arguments = arguments,
    deepLinks = deepLinks,
    content = content
)

class NavHostViewModel @ViewModelInject constructor(
    settingsRepository: SettingsRepository,
    @Assisted savedStateHandle: SavedStateHandle,
) :
    ViewModel() {
    private var navController = MutableStateFlow<NavController?>(null)
    private val _currentScreen = MutableStateFlow<Screen>(HomeScreen.Timeline)
    val currentScreen: StateFlow<Screen>
        get() = _currentScreen

    init {
        val currentScreenKey = "currentScreen"
        val initialScreen = savedStateHandle.get<Screen>(currentScreenKey) ?: HomeScreen.Timeline
        _currentScreen.value = initialScreen
        _currentScreen
            .onEach {
                savedStateHandle.set(currentScreenKey, it)
            }
            .launchIn(viewModelScope)
        if (settingsRepository.twitterToken == null || settingsRepository.twitterTokenSecret == null) {
            navigateReplaceTo(Screen.OAuthLogin)
        }
    }

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, arguments ->
            val logger = Timber.tag("Navigation")
            val screenPath = arguments?.get(KEY_ROUTE)
            val currentScreen = currentScreen.value
            if (arguments == null || screenPath == null) {
                // something's wrong
                logger.e("Path is empty currentScreen: $currentScreen, Destination: $destination, Arguments: $arguments")
                return@OnDestinationChangedListener
            }
            val screenKey = "screen"
            val screen = arguments[screenKey] as? Screen
            if (screen == null && screenPath == currentScreen.path) {
                arguments.putParcelable(screenKey, currentScreen)
            } else if (screen != null) {
                logger.d("Maybe pop backstack currentScreen: $currentScreen, backstackScreen: $screen")
                _currentScreen.value = screen
            }
            logger.d("Destination changed ${this.currentScreen.value}")
        }

    fun attachNavController(lifecycleOwner: LifecycleOwner, navController: NavController) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                this@NavHostViewModel.navController.value = navController
                navController.addOnDestinationChangedListener(destinationChangedListener)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                this@NavHostViewModel.navController.value = null
                navController.removeOnDestinationChangedListener(destinationChangedListener)
            }
        })
    }

    private fun navigate(block: suspend (NavController) -> Unit) {
        navController
            .filterNotNull()
            .take(1)
            .onEach(block)
            .launchIn(viewModelScope)
    }

    fun navigateReplaceTo(screen: Screen) = navigate {
        it.navigate(screen) {
            popUpTo(HomeScreen.Timeline.path) { inclusive = true }
        }
    }

    fun navigateHomeScreen(homeScreen: HomeScreen) = navigate {
        if (currentScreen == homeScreen) {
            return@navigate
        }
        it.navigate(homeScreen) {
            popUpTo(HomeScreen.Timeline.path) { inclusive = homeScreen is HomeScreen.Timeline }
        }
    }

    fun navigateTo(screen: Screen) = navigate {
        it.navigate(screen)
    }

    private fun NavController.navigate(
        screen: Screen,
        builder: NavOptionsBuilder.() -> Unit = {},
    ) {
        _currentScreen.value = screen
        navigate(screen.path, builder)
    }
}