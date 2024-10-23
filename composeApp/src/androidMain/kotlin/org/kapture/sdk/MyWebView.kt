package org.kapture.sdk

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun MyWebView(
    url: String,
    isLoading: (Boolean) -> Unit,
    onUrlClicked: (String) -> Unit,
    modifier: Modifier,
    enableJavaScript: Boolean
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = enableJavaScript
                domStorageEnabled = true
                databaseEnabled = true
                loadsImagesAutomatically = true
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val clickedUrl = request?.url?.toString()
                    if (clickedUrl != null) {
                        onUrlClicked(clickedUrl)
                        // Load the URL in the same WebView
                        view?.loadUrl(clickedUrl)
                    }
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isLoading(true)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isLoading(false)
                }
            }
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    ) { view ->
        view.loadUrl(url)
    }
}
