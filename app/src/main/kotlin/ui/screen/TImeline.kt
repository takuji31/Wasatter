package jp.senchan.android.wasatter.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import jp.senchan.android.wasatter.ui.view.StatusView
import jp.senchan.android.wasatter.viewmodel.TimelineState
import jp.senchan.android.wasatter.viewmodel.TimelineViewModel
import ui.navigation.navViewModel

@Composable
fun Timeline() {
    val viewModel: TimelineViewModel = navViewModel()
    val state: TimelineState by viewModel.state.collectAsState()
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Wasatter") }, actions = {
            IconButton(onClick = { viewModel.reload() }) {
                Icon(imageVector = Icons.Default.Refresh)
            }
        })
    }) {
        when (val state = state) {
            TimelineState.NotLoaded -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is TimelineState.Loaded -> {
                LazyColumn(content = {
                    items(state.responseList) {
                        StatusView(status = it)
                    }
                })
            }
            is TimelineState.LoadFailure -> {

            }
        }
    }
}