package com.adityasaini.edgeviewer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class FrameRenderer: GLSurfaceView.Renderer {
    private var textureId = 0
    @Volatile private var frameBytes: ByteArray? = null
    private var width = 0
    private var height = 0

    fun updateFrame(bytes: ByteArray, w:Int, h:Int) {
        frameBytes = bytes
        width = w
        height = h
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // init shader program, texture
        textureId = createTexture()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        val bytes = frameBytes ?: return
        // Upload bytes to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        val buffer = ByteBuffer.allocateDirect(bytes.size)
        buffer.put(bytes)
        buffer.position(0)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, this.width, this.height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer)
        // draw textured quad (setup VBO, shaders omitted)
    }

    private fun createTexture(): Int {
        val texIds = IntArray(1)
        GLES20.glGenTextures(1, texIds, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        return texIds[0]
    }
}
