package com.example.vulkanfigure

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class VulkanSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var previousX = 0f
    private var previousY = 0f

    init {
        holder.addCallback(this)
        VulkanNative.load()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        VulkanNative.onSurfaceCreated(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        VulkanNative.onSurfaceChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        VulkanNative.onSurfaceDestroyed()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                previousY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - previousX
                val dy = event.y - previousY
                previousX = event.x
                previousY = event.y
                VulkanNative.rotateFigure(dx * 0.01f, dy * 0.01f)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
