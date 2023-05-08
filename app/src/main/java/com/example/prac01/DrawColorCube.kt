package com.example.prac01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DrawColorCube : AppCompatActivity() {
    private lateinit var mainSurface: MainGLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainSurface = MainGLSurfaceView(this)
        setContentView(mainSurface)
    }
}