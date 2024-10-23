package org.kapture.sdk

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalForeignApi::class, ExperimentalObjCName::class)
@ObjCName("MainViewController")
fun MainViewController() = ComposeUIViewController { App() }
