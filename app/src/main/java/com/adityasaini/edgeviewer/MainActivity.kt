package com.adityasaini.edgeviewer

import android.app.Activity
import android.graphics.SurfaceTexture
import android.media.ImageReader
import android.os.Bundle
import android.util.Size
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import android.hardware.camera2.*
import java.nio.ByteBuffer
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var textureView: TextureView
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var reader: ImageReader
    private val previewSize = Size(640,480) // choose size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textureView = findViewById(R.id.textureView)
        // request permissions in real app (omitted here for brevity)

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(st: SurfaceTexture, width: Int, height: Int) {
                setupCamera()
            }
            override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int){}
            override fun onSurfaceTextureDestroyed(st: SurfaceTexture) = true
            override fun onSurfaceTextureUpdated(st: SurfaceTexture){}
        }
    }

    private fun setupCamera(){
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        val camId = manager.cameraIdList[0] // back camera
        val characteristics = manager.getCameraCharacteristics(camId)

        reader = ImageReader.newInstance(previewSize.width, previewSize.height, android.graphics.ImageFormat.YUV_420_888, 2)
        reader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            val yBuffer = image.planes[0].buffer
            val uBuffer = image.planes[1].buffer
            val vBuffer = image.planes[2].buffer

            // Convert YUV_420_888 to NV21 byte[] (Android camera gives Y,U,V planes separately)
            val nv21 = yuv420ToNv21(image, previewSize.width, previewSize.height)

            val out = ByteArray(previewSize.width * previewSize.height * 4)
            NativeLib.processEdge(nv21, previewSize.width, previewSize.height, out)

            // Now 'out' contains RGBA bytes — upload to GL texture or display as Bitmap on TextureView (not optimal)
            // For brevity: code to upload to GL texture will be in GL renderer (see below)

            image.close()
        }, null)

        manager.openCamera(camId, object: CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                val surface = textureView.surfaceTexture.apply {
                    setDefaultBufferSize(previewSize.width, previewSize.height)
                }.let { Surface(it) }

                val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequestBuilder.addTarget(reader.surface)
                captureRequestBuilder.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface, reader.surface), object: CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                        session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                    }
                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                }, null)
            }
            override fun onDisconnected(camera: CameraDevice) {}
            override fun onError(camera: CameraDevice, error: Int) {}
        }, null)
    }

    private fun yuv420ToNv21(image: android.media.Image, width: Int, height: Int): ByteArray {
        // helper to pack Y, U, V into NV21 (this implementation is a known conversion; for brevity assume helper exists)
        // Insert full implementation here - there are many examples online
        val ySize = width * height
        val uvSize = width * height / 2
        val nv21 = ByteArray(ySize + uvSize)
        // copy Y, interleave V and U properly...
        // For the purpose of this guide: use a tested helper function from Android docs or samples.
        return nv21
    }

    private fun yuv420ToNv21(image: Image, width: Int, height: Int): ByteArray {
        val ySize = width * height
        val uvSize = width * height / 2
        val nv21 = ByteArray(ySize + uvSize)

        val yBuffer = image.planes[0].buffer // Y
        val uBuffer = image.planes[1].buffer // U
        val vBuffer = image.planes[2].buffer // V

        yBuffer.get(nv21, 0, ySize)

        val rowStrideU = image.planes[1].rowStride
        val rowStrideV = image.planes[2].rowStride
        val pixelStrideU = image.planes[1].pixelStride
        val pixelStrideV = image.planes[2].pixelStride

        // Interleave V and U into NV21 format (V then U)
        var offset = ySize
        val u = ByteArray(uBuffer.remaining())
        val v = ByteArray(vBuffer.remaining())
        uBuffer.get(u)
        vBuffer.get(v)

        // Subsampling 2x2
        var i = 0
        var p = 0
        while (i < (width * height / 4)) {
            nv21[offset++] = v[p]
            nv21[offset++] = u[p]
            p++
            i++
        }
        return nv21
    }

}
