package com.example.volcanospin

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import kotlin.math.abs

class VolcanoGlSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: VolcanoRenderer
    private var lastX = 0f
    private var lastY = 0f

    init {
        setEGLContextClientVersion(2)
        renderer = VolcanoRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastX
                val dy = event.y - lastY
                if (abs(dx) + abs(dy) > 1f) {
                    renderer.dragRotate(dx * 0.4f, dy * 0.4f)
                }
            }
        }
        lastX = event.x
        lastY = event.y
        return true
    }
}
