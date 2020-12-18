package jp.senchan.android.wasatter.ui.navigation

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.parcelize.Parcelize

sealed class Screen(
    val path: String,
) : Parcelable {
    @Parcelize
    object OAuthLogin : Screen("/settings/oauth/login")
}

sealed class HomeScreen(path: String, val icon: ImageVector, val label: String) : Screen(path) {
    @Parcelize
    object Timeline : HomeScreen("/timeline", Icons.Default.Home, "Home")

    @Parcelize
    object Notifications :
        HomeScreen("/notifications", Icons.Default.Notifications, "Notifications")

    @Parcelize
    object Me : HomeScreen("/me", Icons.Default.Person, "@me")

    @Parcelize
    object Settings : HomeScreen("/settings", Icons.Default.Settings, "Settings")
    companion object {
        val screens: List<HomeScreen> by lazy { listOf(Timeline, Notifications, Me, Settings) }
    }
}
