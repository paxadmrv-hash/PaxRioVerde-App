package com.example.paxrioverde.ui.pet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray?) -> Unit): ImagePickerLauncher {
    return remember {
        object : ImagePickerLauncher {
            override fun launch() {
                // No iOS, a implementação real exigiria interop com PHPickerViewController ou UIImagePickerController.
                // Retornamos null por enquanto para permitir o build.
                onImagePicked(null)
            }
        }
    }
}
