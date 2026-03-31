package com.example.paxrioverde.ui.pet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.*
import platform.UIKit.*
import platform.Foundation.*
import platform.CoreGraphics.*
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

    @OptIn(ExperimentalForeignApi::class)
    override fun launch() {
        val imagePicker = UIImagePickerController()
        imagePicker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        
        val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                if (image != null) {
                    // Redimensionar a imagem para evitar que o Base64 exceda o limite do NSUserDefaults
                    // Seguindo o mesmo padrão do Android (limite de 800px)
                    val resizedImage = resizeImage(image, 800.0)
                    val data = UIImageJPEGRepresentation(resizedImage, 0.6)
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

    @OptIn(ExperimentalForeignApi::class)
    private fun resizeImage(image: UIImage, targetSize: Double): UIImage {
        val size = image.size
        val width = size.useContents { width }
        val height = size.useContents { height }

        if (width <= targetSize && height <= targetSize) return image

        val scale = targetSize / maxOf(width, height)
        val newWidth = width * scale
        val newHeight = height * scale
        
        val newSize = cValue<CGSize> {
            this.width = newWidth
            this.height = newHeight
        }

        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        image.drawInRect(cValue<CGRect> {
            this.origin.x = 0.0
            this.origin.y = 0.0
            this.size.width = newWidth
            this.size.height = newHeight
        })
        val newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return newImage ?: image
    }
}
