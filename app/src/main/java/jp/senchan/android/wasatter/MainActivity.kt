package jp.senchan.android.wasatter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.hilt.lifecycle.HiltViewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.senchan.android.wasatter.ui.navigation.HomeScreen
import jp.senchan.android.wasatter.ui.navigation.NavHostViewModel
import jp.senchan.android.wasatter.ui.navigation.Screen
import jp.senchan.android.wasatter.ui.navigation.screenComposable
import jp.senchan.android.wasatter.ui.screen.LaunchScreen
import jp.senchan.android.wasatter.ui.screen.OAuthLoginScreen
import jp.senchan.android.wasatter.ui.screen.Timeline
import jp.senchan.android.wasatter.ui.screen.WasatterScaffold
import jp.senchan.android.wasatter.ui.theme.WasatterMaterialTheme
import ui.navigation.AmbientApplication
import ui.navigation.AmbientNavController
import ui.navigation.ProvideNavigationViewModelFactoryMap

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            var splashShown by remember { mutableStateOf(SplashState.Shown) }
            val transition = transition(splashTransitionDefinition, splashShown)
            val viewModel: NavHostViewModel = viewModel()
            Providers(
                AmbientApplication provides application,
                AmbientNavController provides navController
            ) {
                ProvideNavigationViewModelFactoryMap(factory = defaultViewModelProviderFactory as HiltViewModelFactory) {
                    WasatterMaterialTheme(isDarkTheme = isSystemInDarkTheme()) {
                        Box {
                            LaunchScreen(
                                modifier = Modifier.alpha(transition[splashAlphaKey]),
                                onTimeout = { splashShown = SplashState.Completed },
                            )
                            Column {
                                Spacer(Modifier.padding(top = transition[contentTopPaddingKey]))
                                Surface(modifier = Modifier.alpha(transition[contentAlphaKey])) {
                                    val currentScreen by viewModel.currentScreen.collectAsState()
                                    val lifecycleOwner = AmbientLifecycleOwner.current

                                    LaunchedEffect(subject = lifecycleOwner) {
                                        viewModel.attachNavController(lifecycleOwner, navController)
                                    }

                                    WasatterScaffold(currentScreen = currentScreen,
                                        onBottomNavigationItemClick = {
                                            viewModel.navigateHomeScreen(it)
                                        }) {
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
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class SplashState { Shown, Completed }

private val splashAlphaKey = FloatPropKey("Splash alpha")
private val contentAlphaKey = FloatPropKey("Content alpha")
private val contentTopPaddingKey = DpPropKey("Top padding")

private val splashTransitionDefinition = transitionDefinition<SplashState> {
    state(SplashState.Shown) {
        this[splashAlphaKey] = 1f
        this[contentAlphaKey] = 0f
        this[contentTopPaddingKey] = 100.dp
    }
    state(SplashState.Completed) {
        this[splashAlphaKey] = 0f
        this[contentAlphaKey] = 1f
        this[contentTopPaddingKey] = 0.dp
    }
    transition {
        splashAlphaKey using tween(
            durationMillis = 100
        )
        contentAlphaKey using tween(
            durationMillis = 300
        )
        contentTopPaddingKey using spring(
            stiffness = Spring.StiffnessLow
        )
    }
}
