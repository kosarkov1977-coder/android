package com.example.volcanospin

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class SolidMesh(private val vertices: FloatArray) {
    private val program: Int
    private val buffer: FloatBuffer = ByteBuffer
        .allocateDirect(vertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer().apply {
            put(vertices)
            position(0)
        }

    init {
        val vertexShader = compileShader(
            GLES20.GL_VERTEX_SHADER,
            """
            uniform mat4 u_MVP;
            uniform mat4 u_Model;
            attribute vec3 a_Pos;
            attribute vec3 a_Normal;
            varying float v_Light;
            void main() {
                vec3 n = normalize((u_Model * vec4(a_Normal, 0.0)).xyz);
                vec3 lightDir = normalize(vec3(0.4, 0.9, 0.2));
                v_Light = max(dot(n, lightDir), 0.2);
                gl_Position = u_MVP * vec4(a_Pos, 1.0);
            }
            """.trimIndent()
        )
        val fragmentShader = compileShader(
            GLES20.GL_FRAGMENT_SHADER,
            """
            precision mediump float;
            uniform vec4 u_Color;
            varying float v_Light;
            void main() {
                gl_FragColor = vec4(u_Color.rgb * v_Light, u_Color.a);
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
        val posLoc = GLES20.glGetAttribLocation(program, "a_Pos")
        val normLoc = GLES20.glGetAttribLocation(program, "a_Normal")
        val mvpLoc = GLES20.glGetUniformLocation(program, "u_MVP")
        val modelLoc = GLES20.glGetUniformLocation(program, "u_Model")
        val colorLoc = GLES20.glGetUniformLocation(program, "u_Color")

        GLES20.glUniformMatrix4fv(mvpLoc, 1, false, mvp, 0)
        GLES20.glUniformMatrix4fv(modelLoc, 1, false, model, 0)
        GLES20.glUniform4fv(colorLoc, 1, color, 0)

        buffer.position(0)
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 6 * 4, buffer)

        buffer.position(3)
        GLES20.glEnableVertexAttribArray(normLoc)
        GLES20.glVertexAttribPointer(normLoc, 3, GLES20.GL_FLOAT, false, 6 * 4, buffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.size / 6)

        GLES20.glDisableVertexAttribArray(posLoc)
        GLES20.glDisableVertexAttribArray(normLoc)
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        return shader
    }
}
