package org.kapture.sdk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier



@Composable
fun App() {
    MaterialTheme {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WebViewScreen()
        }
    }
}


@Composable
fun WebViewScreen() {
    var isLoading by remember { mutableStateOf(false) }

    Column {
        if (isLoading) {
            CircularProgressIndicator()
        }

        MyWebView(
            url = "https://selfserveapp.kapturecrm.com/cb-v1/web-view/webview_chat.html?&data-supportkey=2990d6366ed15c181bc9a016ffd489e6f8eb9b4d5461370534&chat-for=TICKET&data-server=Indian&server-host=ms-noauth&script_type=NR",
            isLoading = { loading -> isLoading = loading },
            onUrlClicked = { url -> println("Clicked URL: $url") },
            modifier = Modifier.fillMaxSize(),
            enableJavaScript = true
        )
    }
}

