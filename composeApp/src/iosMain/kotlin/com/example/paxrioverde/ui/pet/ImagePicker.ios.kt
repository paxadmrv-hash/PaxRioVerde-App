package com.example.paxrioverde.ui.pet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.UIKit.*
import platform.Foundation.*
import platform.darwin.NSObject
import androidx.compose.ui.interop.LocalUIViewController

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray?) -> Unit): ImagePickerLauncher {
    val viewController = LocalUIViewController.current
    return remember(viewController) {
        ImagePickerLauncherImpl(viewController, onImagePicked)
    }
}

class ImagePickerLauncherImpl(
    private val viewController: UIViewController,
    private val onImagePicked: (ByteArray?) -> Unit
) : ImagePickerLauncher {
    
    // Mantemos uma referência forte para o delegate para evitar que o Garbage Collector o remova,
    // já que a propriedade 'delegate' do UIImagePickerController é uma weak reference.
    private var currentDelegate: NSObject? = null

    override fun launch() {
        val imagePicker = UIImagePickerController()
        imagePicker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        
        val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                if (image != null) {
                    val data = UIImageJPEGRepresentation(image, 0.75)
                    if (data != null) {
                        onImagePicked(data.toByteArray())
                    } else {
                        onImagePicked(null)
                    }
                } else {
                    onImagePicked(null)
                }
                picker.dismissViewControllerAnimated(true, null)
                currentDelegate = null
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
                onImagePicked(null)
                currentDelegate = null
            }
        }
        
        this.currentDelegate = delegate
        imagePicker.delegate = delegate
        
        // Apresenta o picker a partir do controlador de visualização atual ou do que estiver no topo
        val topViewController = getTopViewController(viewController)
        topViewController.presentViewController(imagePicker, animated = true, completion = null)
    }

    private fun getTopViewController(root: UIViewController): UIViewController {
        var top = root
        while (top.presentedViewController != null) {
            top = top.presentedViewController!!
        }
        return top
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
