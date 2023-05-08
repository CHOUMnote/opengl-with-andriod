package com.example.prac01

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.prac01.databinding.ActivityDrawObjectsBinding

var isRotating = false
class DrawObjects : AppCompatActivity() {
    val binding:ActivityDrawObjectsBinding by lazy{
        ActivityDrawObjectsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        initGLSurfaceView()
        setContentView(binding.root)

        binding.rotateX.setOnClickListener{
            rotateAxis = 0
        }
        binding.rotateY.setOnClickListener{
            rotateAxis = 1
        }
        binding.rotateZ.setOnClickListener{
            rotateAxis = 2
        }

        binding.toggleBtn.setOnClickListener{
            isRotating = !isRotating
            if(isRotating)
                binding.toggleBtn.text = "Stop"
            else
                binding.toggleBtn.text = "Start"
        }
    }

    private fun initGLSurfaceView() {
        binding.surfaceView.setEGLContextClientVersion(3)
        binding.surfaceView.setRenderer(MainGLRenderer(this))
        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}