package com.example.vulkanfigure

import android.view.Surface

object VulkanNative {
    init {
        load()
    }

    fun load() = runCatching { System.loadLibrary("vulkan_figure") }

    external fun onSurfaceCreated(surface: Surface)
    external fun onSurfaceChanged(width: Int, height: Int)
    external fun onSurfaceDestroyed()
    external fun rotateFigure(yawDelta: Float, pitchDelta: Float)
}
