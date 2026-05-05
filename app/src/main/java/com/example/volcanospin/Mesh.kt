package com.example.volcanospin

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Mesh(private val vertices: FloatArray) {
    private val program: Int
    private val vbo: FloatBuffer = ByteBuffer
        .allocateDirect(vertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(vertices)
            position(0)
        }

    init {
        val vertexShader = compileShader(
            GLES20.GL_VERTEX_SHADER,
            """
            uniform mat4 u_MVP;
            attribute vec3 a_Pos;
            void main() {
                gl_Position = u_MVP * vec4(a_Pos, 1.0);
            }
            """.trimIndent()
        )
        val fragmentShader = compileShader(
            GLES20.GL_FRAGMENT_SHADER,
            """
            precision mediump float;
            uniform vec4 u_Color;
            void main() {
                gl_FragColor = u_Color;
            }
            """.trimIndent()
        )
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    fun draw(vp: FloatArray, model: FloatArray, color: FloatArray) {
        val mvp = FloatArray(16)
        Matrix.multiplyMM(mvp, 0, vp, 0, model, 0)

        GLES20.glUseProgram(program)
        val pos = GLES20.glGetAttribLocation(program, "a_Pos")
        val mvpLoc = GLES20.glGetUniformLocation(program, "u_MVP")
        val colorLoc = GLES20.glGetUniformLocation(program, "u_Color")

        GLES20.glUniformMatrix4fv(mvpLoc, 1, false, mvp, 0)
        GLES20.glUniform4fv(colorLoc, 1, color, 0)
        GLES20.glEnableVertexAttribArray(pos)
        GLES20.glVertexAttribPointer(pos, 3, GLES20.GL_FLOAT, false, 3 * 4, vbo)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertices.size / 3)
        GLES20.glDisableVertexAttribArray(pos)
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        return shader
    }
}
