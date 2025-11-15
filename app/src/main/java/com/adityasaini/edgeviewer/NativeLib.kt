package com.adityasaini.edgeviewer

object NativeLib {
    init {
        System.loadLibrary("native-lib")
    }

    external fun init()
    external fun processEdge(yuv: ByteArray, width: Int, height: Int, out: ByteArray): Boolean
}
