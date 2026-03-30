package com.example.paxrioverde.ui.pet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.Foundation.NSData
import platform.Foundation.getBytes
import platform.darwin.NSObject
import platform.UIKit.UIApplication
import androidx.compose.ui.interop.LocalUIViewController

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray?) -> Unit): ImagePickerLauncher {
    val viewController = LocalUIViewController.current
    return remember {
        ImagePickerLauncherImpl(viewController, onImagePicked)
    }
}

class ImagePickerLauncherImpl(
    private val viewController: UIViewController,
    private val onImagePicked: (ByteArray?) -> Unit
) : ImagePickerLauncher {
    override fun launch() {
        val imagePicker = UIImagePickerController()
        imagePicker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        imagePicker.delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
                val image = didFinishPickingMediaWithInfo[platform.UIKit.UIImagePickerControllerOriginalImage] as? UIImage
                if (image != null) {
                    val data = UIImageJPEGRepresentation(image, 0.8)
                    if (data != null) {
                        onImagePicked(data.toByteArray())
                    } else {
                        onImagePicked(null)
                    }
                } else {
                    onImagePicked(null)
                }
                picker.dismissViewControllerAnimated(true, null)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
                onImagePicked(null)
            }
        }
        viewController.presentViewController(imagePicker, true, null)
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val byteArray = ByteArray(size)
    if (size > 0) {
        byteArray.usePinned { pinned ->
            getBytes(pinned.addressOf(0), length)
        }
    }
    return byteArray
}
