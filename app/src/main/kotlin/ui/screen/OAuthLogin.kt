package jp.senchan.android.wasatter.ui.screen

import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.viewModel
import jp.senchan.android.wasatter.viewmodel.OAuthLoginViewModel

@Composable
fun OAuthLoginScreen() {
    val vieModel: OAuthLoginViewModel = viewModel()
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "ログイン") }) },
    ) {
        Snackbar {
            
        }
    }
}

@Composable
@Preview
fun PreviewOAuthLoginScreen() {
    OAuthLoginScreen()
}