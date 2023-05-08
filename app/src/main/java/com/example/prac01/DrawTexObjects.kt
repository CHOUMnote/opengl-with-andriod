package com.example.prac01

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.prac01.databinding.ActivityDrawTexObjectsBinding

class DrawTexObjects : AppCompatActivity() {
    val binding: ActivityDrawTexObjectsBinding by lazy{
        ActivityDrawTexObjectsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_draw_tex_objects)

        supportActionBar?.hide()
        initSurfaceView()
        setContentView(binding.root)
    }

    fun initSurfaceView(){
        binding.surfaceView.setEGLContextClientVersion(3)
        binding.surfaceView.setRenderer(MainGLRenderer(this))
        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

}