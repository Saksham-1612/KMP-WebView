package org.kapture.sdk

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MyWebView(
    url: String,
    isLoading: (Boolean) -> Unit,
    onUrlClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    enableJavaScript: Boolean = true
)