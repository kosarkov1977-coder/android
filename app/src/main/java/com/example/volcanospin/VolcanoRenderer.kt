package com.example.volcanospin

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VolcanoRenderer : GLSurfaceView.Renderer {
    private val projection = FloatArray(16)
    private val view = FloatArray(16)
    private val vp = FloatArray(16)

    private lateinit var volcano: Mesh
    private lateinit var figure: Mesh

    @Volatile
    private var userYaw = 0f

    @Volatile
    private var userPitch = 0f

    private var autoRot = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.04f, 0.05f, 0.1f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        volcano = MeshFactory.createVolcanoCone()
        figure = MeshFactory.createTorusKnot()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projection, 0, 50f, ratio, 0.1f, 100f)
        Matrix.setLookAtM(view, 0, 0f, 2.5f, 8f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(vp, 0, projection, 0, view, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        autoRot += 0.9f

        val modelVolcano = FloatArray(16)
        Matrix.setIdentityM(modelVolcano, 0)
        Matrix.translateM(modelVolcano, 0, 0f, -1.5f, 0f)
        Matrix.rotateM(modelVolcano, 0, -90f, 1f, 0f, 0f)
        volcano.draw(vp, modelVolcano, floatArrayOf(0.35f, 0.2f, 0.1f, 1f))

        val modelFigure = FloatArray(16)
        Matrix.setIdentityM(modelFigure, 0)
        Matrix.translateM(modelFigure, 0, 0f, 1.1f, 0f)
        Matrix.rotateM(modelFigure, 0, autoRot + userYaw, 0f, 1f, 0f)
        Matrix.rotateM(modelFigure, 0, userPitch, 1f, 0f, 0f)
        figure.draw(vp, modelFigure, floatArrayOf(0.96f, 0.45f, 0.18f, 1f))
    }

    fun dragRotate(dx: Float, dy: Float) {
        userYaw += dx
        userPitch = (userPitch + dy).coerceIn(-80f, 80f)
    }
}
