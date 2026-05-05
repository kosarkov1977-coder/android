package com.example.volcanospin

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object MeshFactory {
    fun createVolcanoCone(segments: Int = 36): Mesh {
        val points = mutableListOf<Float>()
        val radius = 2.2f
        val crater = 0.8f
        val h = -1.6f

        for (i in 0 until segments) {
            val a0 = (2.0 * PI * i / segments).toFloat()
            val a1 = (2.0 * PI * (i + 1) / segments).toFloat()

            val x0 = cos(a0) * radius
            val z0 = sin(a0) * radius
            val x1 = cos(a1) * radius
            val z1 = sin(a1) * radius

            addLine(points, x0.toFloat(), h, z0.toFloat(), x1.toFloat(), h, z1.toFloat())
            addLine(points, x0.toFloat(), h, z0.toFloat(), (x0 * crater / radius).toFloat(), 0f, (z0 * crater / radius).toFloat())
        }
        return Mesh(points.toFloatArray())
    }

    fun createTorusKnot(steps: Int = 320): Mesh {
        val points = mutableListOf<Float>()
        var prev = point(0f)
        for (i in 1..steps) {
            val t = (2f * PI.toFloat() * i / steps)
            val current = point(t)
            addLine(points, prev[0], prev[1], prev[2], current[0], current[1], current[2])
            prev = current
        }
        return Mesh(points.toFloatArray())
    }

    private fun point(t: Float): FloatArray {
        val p = 3f
        val q = 2f
        val r = 1.4f + 0.5f * cos(q * t)
        val x = r * cos(p * t)
        val y = 0.8f * sin(q * t)
        val z = r * sin(p * t)
        return floatArrayOf(x, y, z)
    }

    private fun addLine(out: MutableList<Float>, x0: Float, y0: Float, z0: Float, x1: Float, y1: Float, z1: Float) {
        out.add(x0); out.add(y0); out.add(z0)
        out.add(x1); out.add(y1); out.add(z1)
    }
}
