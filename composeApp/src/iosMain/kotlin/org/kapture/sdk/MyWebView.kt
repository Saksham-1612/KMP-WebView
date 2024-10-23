package org.kapture.sdk

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.UIKit.UIView
import platform.WebKit.*
import platform.CoreGraphics.*
import platform.QuartzCore.CATransaction
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.QuartzCore.kCATransactionDisableActions
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MyWebView(
    url: String,
    isLoading: (Boolean) -> Unit,
    onUrlClicked: (String) -> Unit,
    modifier: Modifier,
    enableJavaScript: Boolean
) {
    val configuration = remember {
        WKWebViewConfiguration().apply {
            preferences.javaScriptEnabled = enableJavaScript
            preferences.setJavaScriptCanOpenWindowsAutomatically(true)
        }
    }

    val initialFrame = CGRectMake(0.0, 0.0, 0.0, 0.0)

    val webView = remember {
        WKWebView(frame = initialFrame, configuration = configuration).apply {
            setAllowsBackForwardNavigationGestures(true)
            setAllowsLinkPreview(true)
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.navigationDelegate = null
        }
    }

    val navigationDelegate = rememberWebViewDelegate(
        onUrlClicked = onUrlClicked,
        onLoadingStateChanged = isLoading
    )

    webView.navigationDelegate = navigationDelegate

    UIKitView(
        modifier = modifier,
        factory = {
            val container = UIView(frame = initialFrame)

            val nsUrl = NSURL(string = url) ?: run {
                println("Invalid URL provided: $url")
                return@UIKitView container
            }

            val request = NSURLRequest(nsUrl)
            webView.loadRequest(request)

            container.addSubview(webView)
            container
        },
        onResize = { view: UIView, rect: CValue<CGRect> ->
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)

            // Set frames for both container and webView
            view.layer.setFrame(rect)
            webView.setFrame(rect)

            CATransaction.commit()
        }
    )
}

@Composable
private fun rememberWebViewDelegate(
    onUrlClicked: (String) -> Unit,
    onLoadingStateChanged: (Boolean) -> Unit
): WKNavigationDelegateProtocol {
    return remember {
        object : NSObject(), WKNavigationDelegateProtocol {
            override fun webView(
                webView: WKWebView,
                decidePolicyForNavigationAction: WKNavigationAction,
                decisionHandler: (WKNavigationActionPolicy) -> Unit
            ) {
                val navigationType = decidePolicyForNavigationAction.navigationType
                val request = decidePolicyForNavigationAction.request

                when (navigationType) {
                    WKNavigationTypeLinkActivated -> {
                        val urlString = request.URL?.absoluteString
                        if (urlString != null) {
                            onUrlClicked(urlString)
                            if (decidePolicyForNavigationAction.targetFrame == null) {
                                webView.loadRequest(request)
                            }
                        }
                        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
                    }
                    else -> decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
                }
            }


            override fun webView(webView: WKWebView, didFailNavigation: WKNavigation?, withError: NSError) {
                onLoadingStateChanged(false)
                println("Navigation failed with error: ${withError.localizedDescription}")
            }
        }
    }
}