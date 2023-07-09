package kono.display

import kono.runtime.window.NativeWindow
import kono.runtime.window.WindowWrapper

internal fun Window(window: NativeWindow) = WindowWrapper(window)

/**
 * Represents a platform-native window
 */
interface Window {
}