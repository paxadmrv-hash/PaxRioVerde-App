package com.example.paxrioverde.util

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum
import platform.Foundation.NSData
import platform.Foundation.create
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.BetaInteropApi

actual fun shareText(text: String, title: String) {
    val window = UIApplication.sharedApplication.keyWindow ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
    val rootViewController = window?.rootViewController

    val activityViewController = UIActivityViewController(
        activityItems = listOf(text),
        applicationActivities = null
    )

    rootViewController?.presentViewController(
        viewControllerToPresent = activityViewController,
        animated = true,
        completion = null
    )
}

@OptIn(ExperimentalForeignApi::class)
actual fun saveImageToGallery(bytes: ByteArray, fileName: String) {
    val nsData = bytes.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = bytes.size.toULong()
        )
    }
    val image = nsData?.let { UIImage.imageWithData(it) } ?: return
    
    UIImageWriteToSavedPhotosAlbum(image, null, null, null)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun shareImage(bytes: ByteArray, fileName: String, title: String) {
    val window = UIApplication.sharedApplication.keyWindow ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
    val rootViewController = window?.rootViewController

    val nsData = bytes.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = bytes.size.toULong()
        )
    }
    
    val image = nsData?.let { UIImage.imageWithData(it) } ?: return
    
    val activityViewController = UIActivityViewController(
        activityItems = listOf(image),
        applicationActivities = null
    )

    rootViewController?.presentViewController(
        viewControllerToPresent = activityViewController,
        animated = true,
        completion = null
    )
}
