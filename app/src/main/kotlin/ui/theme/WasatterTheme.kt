package jp.senchan.android.wasatter.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun WasatterMaterialTheme(isDarkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isDarkTheme) darkColors(
            primary = Colors.primaryLight,
            primaryVariant = Colors.primaryDark,
            secondary = Colors.secondaryLight,
        ) else lightColors(
            primary = Colors.primary,
            primaryVariant = Colors.primaryDark,
            secondary = Colors.secondary,
        ),
        content = content,
    )
}