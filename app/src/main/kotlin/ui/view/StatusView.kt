package jp.senchan.android.wasatter.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.Message
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.coil.CoilImage
import jp.senchan.android.wasatter.ui.theme.WasatterMaterialTheme
import twitter4j.Status

@Composable
fun StatusView(status: Status) {
    StatusViewContent(iconUrl = status.user.profileImageURLHttps,
        screenName = status.user.screenName,
        userName = status.user.name,
        body = status.text,
        isFavorited = status.isFavorited,
        isRetweeted = status.isRetweetedByMe,
        hasReply = false)
}

@Composable
fun StatusViewContent(
    iconUrl: String,
    screenName: String,
    userName: String,
    body: String,
    isFavorited: Boolean,
    isRetweeted: Boolean,
    hasReply: Boolean,
) {
    Surface(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
        Row {
            CoilImage(data = iconUrl, modifier = Modifier.size(60.dp).clip(CircleShape), loading = {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            })
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Row {
                    Text(text = userName, style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.preferredSize(8.dp))
                    Text(text = screenName, style = MaterialTheme.typography.body1)
                }
                Spacer(modifier = Modifier.preferredSize(8.dp))
                Text(text = body, style = MaterialTheme.typography.body2)

                Spacer(modifier = Modifier.preferredSize(8.dp))
                Row {
                    TextButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Message)
                    }
                    TextButton(onClick = { /*TODO*/ }) {
                        if (isFavorited) {
                            Icon(Icons.Filled.Favorite)
                        } else {
                            Icon(Icons.Filled.FavoriteBorder)
                        }
                    }
                    TextButton(onClick = { /*TODO*/ }) {
                        val color = if (isRetweeted) {
                            MaterialTheme.colors.secondary
                        } else {
                            MaterialTheme.colors.primary
                        }
                        Icon(Icons.Filled.Repeat, tint = color)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewStatusView() {
    WasatterMaterialTheme {
        StatusViewContent(
            iconUrl = "https://pbs.twimg.com/profile_images/904622407863885825/IllN-0iZ_400x400.jpg",
            screenName = "@takuji31",
            userName = "たくじ",
            body = "こんにちはこんにちはこんにちはこんにちはこんにちはこんにちはこんにちはこんにちはこんにちはこんにちはこんにちは",
            isFavorited = false,
            isRetweeted = false,
            hasReply = true)
    }
}