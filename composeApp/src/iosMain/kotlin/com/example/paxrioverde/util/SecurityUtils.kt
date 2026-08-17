package com.example.paxrioverde.util

import platform.UIKit.*
import platform.Foundation.*

/**
 * No iOS, o FLAG_SECURE não existe da mesma forma.
 * Uma abordagem comum é adicionar um blur ou imagem de overlay quando o app vai para o background.
 */
actual fun setScreenSecurity(enabled: Boolean) {
    // Implementação para iOS
}

actual fun isDeviceRooted(): Boolean {
    val fileManager = NSFileManager.defaultManager()
    val jailbreakPaths = listOf(
        "/Applications/Cydia.app",
        "/Library/MobileSubstrate/MobileSubstrate.dylib",
        "/bin/bash",
        "/usr/sbin/sshd",
        "/etc/apt",
        "/private/var/lib/apt/"
    )

    for (path in jailbreakPaths) {
        if (fileManager.fileExistsAtPath(path)) return true
    }

    return false
}

