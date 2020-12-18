package jp.senchan.android.wasatter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import androidx.navigation.compose.rememberNavController
import jp.senchan.android.wasatter.ui.screen.OAuthLoginScreen
import jp.senchan.android.wasatter.ui.screen.Timeline
import jp.senchan.android.wasatter.ui.screen.WasatterScaffold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import timber.log.Timber

@Composable
fun WasatterNavHost() {
    val viewModel: NavHostViewModel = viewModel()
    val navController = rememberNavController()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val lifecycleOwner = AmbientLifecycleOwner.current

    LaunchedEffect(subject = lifecycleOwner) {
        viewModel.attachNavController(lifecycleOwner, navController)
    }

    WasatterScaffold(currentScreen = currentScreen,
        onBottomNavigationItemClick = { viewModel.navigateHomeScreen(it) }) {
        NavHost(navController = navController,
            startDestination = HomeScreen.Timeline.path) {
            screenComposable(HomeScreen.Timeline) {
                Timeline()
            }
            screenComposable(HomeScreen.Notifications) {
                Timeline()
            }
            screenComposable(HomeScreen.Me) {
                Timeline()
            }
            screenComposable(HomeScreen.Settings) {
                Timeline()
            }
            screenComposable(Screen.OAuthLogin) {
                OAuthLoginScreen()
            }
        }
    }
}

private fun NavGraphBuilder.screenComposable(
    screen: Screen,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit,
) = composable(
    screen.path,
    arguments = listOf(navArgument("path") {
        defaultValue = screen.path
    }) + arguments,
    deepLinks = deepLinks,
    content = content
)

class NavHostViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
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
    }

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, arguments ->
            val logger = Timber.tag("Navigation")
            val screenPath = arguments?.get("path")
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

    fun navigateHomeScreen(homeScreen: HomeScreen) = navigate {
        if (currentScreen == homeScreen) {
            return@navigate
        }
        _currentScreen.value = homeScreen
        it.navigate(homeScreen.path) {
            popUpTo(HomeScreen.Timeline.path) { inclusive = homeScreen is HomeScreen.Timeline }
        }
    }

    fun navigateTo(screen: Screen) = navigate {
        _currentScreen.value = screen
        it.navigate(screen.path)
    }
}