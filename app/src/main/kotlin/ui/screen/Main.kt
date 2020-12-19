package jp.senchan.android.wasatter.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import jp.senchan.android.wasatter.ui.navigation.HomeScreen
import jp.senchan.android.wasatter.ui.navigation.Screen
import jp.senchan.android.wasatter.ui.theme.WasatterMaterialTheme

@Composable
fun Main(modifier: Modifier = Modifier, topPadding: Dp) {
}

@Composable
fun WasatterScaffold(
    currentScreen: Screen,
    onBottomNavigationItemClick: (HomeScreen) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        bottomBar = {
            if (currentScreen is HomeScreen) {
                MainBottomNavigation(currentScreen = currentScreen,
                    onItemClick = onBottomNavigationItemClick)
            }
        }, bodyContent = content)
}

@Composable
fun MainBottomNavigation(
    currentScreen: Screen,
    onItemClick: (HomeScreen) -> Unit,
) {
    BottomNavigation {
        HomeScreen.screens.forEach {
            BottomNavigationItem(
                icon = {
                    Icon(it.icon)
                },
                label = { Text(text = it.label) },
                selected = currentScreen == it,
                onClick = {
                    onItemClick(it)
                }
            )
        }
    }
}

@Composable
@Preview
fun PreviewMainButtomNavigation() = WasatterMaterialTheme {
    MainBottomNavigation(currentScreen = HomeScreen.Timeline, onItemClick = {})
}
