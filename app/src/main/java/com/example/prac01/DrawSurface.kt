package com.example.prac01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DrawSurface : AppCompatActivity() {
    private lateinit var mainSurfaceView:MainGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainSurfaceView = MainGLSurfaceView(this)
        setContentView(mainSurfaceView)
    }
}