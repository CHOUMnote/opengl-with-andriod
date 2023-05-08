package com.example.prac01

import android.content.Context
import android.opengl.GLSurfaceView

class MainGLSurfaceView(context: Context): GLSurfaceView(context) {
    private val mainRenderer:MainGLRenderer
    init{
        setEGLContextClientVersion(3)
        mainRenderer = MainGLRenderer(context)
        setRenderer(mainRenderer)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }
}