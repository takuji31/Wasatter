package jp.senchan.android.wasatter.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.senchan.android.wasatter.R
import kotlinx.coroutines.delay

private const val SplashWaitTime: Long = 2000

@Composable
fun LaunchScreen(modifier: Modifier = Modifier, onTimeout: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            // Adds composition consistency. Use the value when LaunchedEffect is first called
            val currentOnTimeout by rememberUpdatedState(onTimeout)

            LaunchedEffect(Unit) {
                delay(SplashWaitTime)
                currentOnTimeout()
            }
            Image(imageResource(id = R.drawable.wasattericon))
            Text(text = stringResource(R.string.app_name))
        }
    }
}

@Composable
@Preview
fun PreviewLaunchScreen() {
    LaunchScreen(onTimeout = { /*TODO*/ })
}