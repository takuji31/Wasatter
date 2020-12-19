package ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.viewinterop.viewModel
import androidx.hilt.lifecycle.HiltViewModelFactory
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

val AmbientApplication = staticAmbientOf<Application>()
val AmbientViewModelProviderFactory = ambientOf<ViewModelProvider.Factory>()
val AmbientNavController = ambientOf<NavHostController>()
val AmbientViewModelFactoriesMap =
    ambientOf<Map<String, ViewModelAssistedFactory<out ViewModel>>>()

/**
 * from https://github.com/google/dagger/issues/2166#issuecomment-723775543
 */
@Composable
inline fun <reified VM : ViewModel> navViewModel(
    key: String? = null,
    factory: ViewModelProvider.Factory? = AmbientViewModelProviderFactory.current,
): VM {
    val navController = AmbientNavController.current
    val backStackEntry = navController.currentBackStackEntryAsState().value
    return if (backStackEntry != null) {
        // Hack for navigation viewModel
        val application = AmbientApplication.current
        val viewModelFactories = AmbientViewModelFactoriesMap.current
        val delegate = SavedStateViewModelFactory(application, backStackEntry, null)
        val hiltViewModelFactory = HiltViewModelFactory::class.java.declaredConstructors.first()
            .newInstance(backStackEntry, null, delegate, viewModelFactories) as HiltViewModelFactory
        viewModel(key, hiltViewModelFactory)
    } else {
        viewModel(key, factory)
    }
}

@Composable
fun ProvideNavigationViewModelFactoryMap(
    factory: HiltViewModelFactory,
    content: @Composable () -> Unit,
) {
    // Hack for navigation viewModel
    val factories =
        HiltViewModelFactory::class.java.getDeclaredField("mViewModelFactories")
            .also { it.isAccessible = true }
            .get(factory).let {
                it as Map<String, ViewModelAssistedFactory<out ViewModel>>
            }
    Providers(
        AmbientViewModelFactoriesMap provides factories
    ) {
        content.invoke()
    }
}
