package com.example.volcanospin

import kotlin.math.sqrt

object PolyhedronFactory {
    fun createIcosahedron(radius: Float = 1.25f): SolidMesh {
        val t = ((1.0 + sqrt(5.0)) / 2.0).toFloat()
        val raw = arrayOf(
            floatArrayOf(-1f, t, 0f), floatArrayOf(1f, t, 0f), floatArrayOf(-1f, -t, 0f), floatArrayOf(1f, -t, 0f),
            floatArrayOf(0f, -1f, t), floatArrayOf(0f, 1f, t), floatArrayOf(0f, -1f, -t), floatArrayOf(0f, 1f, -t),
            floatArrayOf(t, 0f, -1f), floatArrayOf(t, 0f, 1f), floatArrayOf(-t, 0f, -1f), floatArrayOf(-t, 0f, 1f)
        ).map { normalizeAndScale(it, radius) }

        val faces = arrayOf(
            intArrayOf(0, 11, 5), intArrayOf(0, 5, 1), intArrayOf(0, 1, 7), intArrayOf(0, 7, 10), intArrayOf(0, 10, 11),
            intArrayOf(1, 5, 9), intArrayOf(5, 11, 4), intArrayOf(11, 10, 2), intArrayOf(10, 7, 6), intArrayOf(7, 1, 8),
            intArrayOf(3, 9, 4), intArrayOf(3, 4, 2), intArrayOf(3, 2, 6), intArrayOf(3, 6, 8), intArrayOf(3, 8, 9),
            intArrayOf(4, 9, 5), intArrayOf(2, 4, 11), intArrayOf(6, 2, 10), intArrayOf(8, 6, 7), intArrayOf(9, 8, 1)
        )

        val out = ArrayList<Float>(faces.size * 18)
        for (f in faces) {
            val v0 = raw[f[0]]
            val v1 = raw[f[1]]
            val v2 = raw[f[2]]
            val n = faceNormal(v0, v1, v2)
            addVertex(out, v0, n)
            addVertex(out, v1, n)
            addVertex(out, v2, n)
        }
        return SolidMesh(out.toFloatArray())
    }

    private fun normalizeAndScale(v: FloatArray, s: Float): FloatArray {
        val len = sqrt((v[0] * v[0] + v[1] * v[1] + v[2] * v[2]).toDouble()).toFloat()
        return floatArrayOf(v[0] / len * s, v[1] / len * s, v[2] / len * s)
    }

    private fun faceNormal(a: FloatArray, b: FloatArray, c: FloatArray): FloatArray {
        val ux = b[0] - a[0]; val uy = b[1] - a[1]; val uz = b[2] - a[2]
        val vx = c[0] - a[0]; val vy = c[1] - a[1]; val vz = c[2] - a[2]
        val nx = uy * vz - uz * vy
        val ny = uz * vx - ux * vz
        val nz = ux * vy - uy * vx
        val len = sqrt((nx * nx + ny * ny + nz * nz).toDouble()).toFloat().coerceAtLeast(1e-6f)
        return floatArrayOf(nx / len, ny / len, nz / len)
    }

    private fun addVertex(out: MutableList<Float>, p: FloatArray, n: FloatArray) {
        out.add(p[0]); out.add(p[1]); out.add(p[2])
        out.add(n[0]); out.add(n[1]); out.add(n[2])
    }
}
